// delivery_system.order.application.CartService.java (최종 수정본)
package delivery_system.cart.application;

import delivery_system.cart.domain.Entity.Cart;
import delivery_system.cart.domain.Entity.CartItem;
import delivery_system.cart.domain.Entity.CartItemOpt;
import delivery_system.cart.domain.repository.CartItemRepository;
import delivery_system.cart.domain.repository.CartRepository;
import delivery_system.cart.presentation.dto.CartAddItemRequest;
import delivery_system.cart.presentation.dto.CartDto;
import delivery_system.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionValueRepository menuOptionValueRepository;
    private final StoreRepository storeRepository;
    private final MenuOptionRepository menuOptionRepository; // 옵션 이름 조회를 위해

    // ------------------------------------------------------------------
    // 헬퍼 1: 금액 재계산 로직 (DDL에 맞춰 Integer 타입에 대한 null 방지 추가)
    // ------------------------------------------------------------------
    private void updateCartFees(Cart cart) {
        int itemFee = 0;
        for (CartItem item : cart.getItems()) {
            int itemPrice = item.getCartItemFee();
            for (CartItemOpt opt : item.getOptions()) {
                itemPrice += opt.getMenuOptValueFee();
            }
            itemFee += itemPrice * item.getCartItemQuantity();
        }

        cart.setItemFee(itemFee);

        // DDL: coupon_fee는 null 허용. null이면 0으로 처리
        int couponFee = cart.getCouponFee() == null ? 0 : cart.getCouponFee();

        // DDL: delivery_fee, item_fee, total_fee는 NOT NULL
        int totalFee = itemFee - couponFee + cart.getDeliveryFee();
        cart.setTotalFee(totalFee);
    }

    // ------------------------------------------------------------------
    // 헬퍼 2: 메뉴/가게/옵션 DB 조회 및 검증
    // ------------------------------------------------------------------
    private Menu getMenu(UUID menuId) {
        return menuRepository.findByMenuIdAndDeletedAtIsNull(menuId)
                .orElseThrow(() -> new MenuInfoNotFoundException("메뉴(ID: " + menuId + ")"));
    }

    private Store getStore(UUID storeId) {
        return storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new MenuInfoNotFoundException("가게(ID: " + storeId + ")"));
    }

    private Map<UUID, MenuOptionValue> getOptionValuesMap(List<UUID> optValueIds) {
        if (optValueIds == null || optValueIds.isEmpty()) {
            return Map.of();
        }

        List<MenuOptionValue> optionValues = menuOptionValueRepository
                .findAllByMenuOptValueIdInAndDeletedAtIsNull(optValueIds);

        if (optionValues.size() != optValueIds.size()) {
            throw new MenuInfoNotFoundException("하나 이상의 옵션 값");
        }

        return optionValues.stream()
                .collect(Collectors.toMap(MenuOptionValue::getMenuOptValueId, value -> value));
    }

    private String getOptionGroupName(UUID menuOptId) {
        MenuOption optGroup = menuOptionRepository.findByMenuOptIdAndDeletedAtIsNull(menuOptId)
                .orElse(null); // 옵션 그룹은 필수 정보가 아닐 수 있으므로 null 허용
        return optGroup != null ? optGroup.getMenuOptName() : "알 수 없는 옵션 그룹";
    }

    // ------------------------------------------------------------------
    // 1. 항목 등록: POST /api/v1/cart
    // ------------------------------------------------------------------
    @Transactional
    public CartDto addItemToCart(String userId, CartAddItemRequest request) {

        // 1) 메뉴, 가게 정보 DB에서 조회 및 검증
        Menu menu = getMenu(request.getMenuId());
        Store store = getStore(request.getStoreId());

        if (!menu.getStoreId().equals(store.getStoreId())) {
            throw new MenuInfoNotFoundException("요청된 가게에 해당 메뉴가 없습니다.");
        }

        // 2) 옵션 정보 DB에서 조회 및 Map으로 정리
        List<UUID> optValueIds = request.getOptions() != null ?
                request.getOptions().stream().map(CartAddItemRequest.OptionDto::getMenuOptValueId).collect(Collectors.toList()) :
                List.of();

        Map<UUID, MenuOptionValue> selectedOptValues = getOptionValuesMap(optValueIds);

        // 3) 장바구니 조회 또는 생성 (가게 충돌 검증)
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart != null) {
            if (!cart.getStoreId().equals(request.getStoreId())) {
                // 기존 장바구니 가게와 다를 경우 예외 발생
                Store currentStore = getStore(cart.getStoreId());
                throw new CartStoreConflictException(currentStore.getStoreName(), store.getStoreName());
            }
        } else {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID());
            cart.setUserId(userId);
            cart.setStoreId(request.getStoreId());
            cart.setDeliveryFee(store.getDeliveryFee()); // 가게 배달비 적용
            cart.setCouponFee(0);
        }

        // 4) CartItem 및 CartItemOpt 생성/추가 (DB 조회된 가격과 이름 사용)
        CartItem newItem = new CartItem();
        newItem.setCartItemId(UUID.randomUUID());
        newItem.setCart(cart);
        newItem.setMenuId(menu.getMenuId());
        newItem.setMenuName(menu.getMenuName());
        newItem.setCartItemFee(menu.getMenuFee());
        newItem.setCartItemQuantity(request.getQuantity());

        // 옵션 설정
        for (CartAddItemRequest.OptionDto optReq : request.getOptions()) {
            MenuOptionValue optValue = selectedOptValues.get(optReq.getMenuOptValueId());

            CartItemOpt opt = new CartItemOpt();
            opt.setCartItemOptId(UUID.randomUUID());
            opt.setCartItem(newItem);
            opt.setMenuOptId(optValue.getMenuOptId());
            opt.setMenuOptName(getOptionGroupName(optValue.getMenuOptId()));
            opt.setMenuOptValueId(optValue.getMenuOptValueId());
            opt.setMenuOptValueName(optValue.getMenuOptValueName());
            opt.setMenuOptValueFee(optValue.getMenuOptValueFee());
            newItem.getOptions().add(opt);
        }

        cart.getItems().add(newItem);

        // 5) 금액 업데이트 및 저장
        updateCartFees(cart);
        return CartDto.from(cartRepository.save(cart));
    }

    // ------------------------------------------------------------------
    // 5. 항목 옵션 수정: PATCH /api/v1/cart/items/{cart_item_id}/opts/{cart_item_opt_id}
    // ------------------------------------------------------------------
    @Transactional
    public CartDto updateCartItemOption(String userId, UUID cartItemId, UUID cartItemOptId, UUID newOptValueId) {
        CartItem item = cartItemRepository.findByCartItemIdAndCart_UserId(cartItemId, userId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 항목"));

        CartItemOpt targetOpt = item.getOptions().stream()
                .filter(opt -> opt.getCartItemOptId().equals(cartItemOptId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 옵션 항목"));

        // 1. 새로운 옵션 값에 대한 정보 DB에서 조회
        MenuOptionValue newOptValue = menuOptionValueRepository.findByMenuOptValueIdAndDeletedAtIsNull(newOptValueId)
                .orElseThrow(() -> new MenuInfoNotFoundException("새로운 옵션 값(ID: " + newOptValueId + ")"));

        // 2. 같은 옵션 그룹 내 변경인지 검증
        if (!targetOpt.getMenuOptId().equals(newOptValue.getMenuOptId())) {
            throw new CartException("다른 옵션 그룹으로 변경할 수 없습니다.");
        }

        // 3. 옵션 정보 업데이트 (DB에 복사된 값 수정)
        targetOpt.setMenuOptValueId(newOptValue.getMenuOptValueId());
        targetOpt.setMenuOptValueName(newOptValue.getMenuOptValueName());
        targetOpt.setMenuOptValueFee(newOptValue.getMenuOptValueFee());

        Cart cart = item.getCart();
        updateCartFees(cart);
        return CartDto.from(cartRepository.save(cart));
    }

    // ------------------------------------------------------------------
    // 나머지 Service 메서드 (updateItemQuantity, deleteCartItem, addCartItemOptions, deleteCartItemOption)
    // - 이 메서드들은 DB 조회 로직이 MenuServiceStub에 의존하지 않으므로 수정 불필요하며,
    // - 금액 계산 시 항상 updateCartFees()를 호출하도록 이미 구현되어 있습니다.
    // ------------------------------------------------------------------
}