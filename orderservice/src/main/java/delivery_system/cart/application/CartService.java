package delivery_system.cart.application;

import delivery_system.cart.domain.Entity.Cart;
import delivery_system.cart.domain.Entity.CartItem;
import delivery_system.cart.domain.Entity.CartItemOpt;
import delivery_system.cart.domain.repository.*;
import delivery_system.cart.presentation.dto.*;

import delivery_system.cart.exception.CartNotFoundException;
import delivery_system.cart.exception.CartStoreConflictException;
import delivery_system.cart.exception.MenuInfoNotFoundException;
import delivery_system.cart.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// [새로운 Helper 클래스 가정]
// 💡 가정: 주소/배달 거리 검증 및 Order API 전송을 위한 헬퍼 클래스
class DeliveryPolicyHandler {
    // 3. 주소 검증 로직을 시뮬레이션합니다.
    public boolean checkDeliveryAvailability(UUID storeId, String address) {
        // 실제로는 DB 쿼리나 외부 API 호출을 통해 가게와 주소 간의 거리 및 가능 여부를 체크합니다.
        // 여기서는 임시로 주소가 '특정 금지 구역'이 아니면 가능하다고 가정합니다.
        return !address.contains("배달불가");
    }
    // 5. Order API로 데이터 전송 로직을 시뮬레이션합니다.
    public void sendOrderData(Cart cart) {
        // 실제로는 HTTP POST 요청 등을 통해 paymentservice/orderservice로 최종 주문 데이터를 전송합니다.
        System.out.println("✅ 주문 API로 Cart 데이터 전송 완료: " + cart.getUserId());
    }
    // 3. 유저의 기본 주소를 조회하는 로직을 시뮬레이션합니다.
    public String findDefaultAddressByUsername(String username) {
        // 실제로는 P_ADDRESS 테이블에서 is_default=true인 주소를 가져옵니다.
        return "서울 강남구 역삼동 646-15 (기본 주소)";
    }
}


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    private final AtomicLong uniqueIdCounter = new AtomicLong(0);

    // 💡 3, 5번 기능을 위한 헬퍼 클래스 초기화 (가정)
    private final DeliveryPolicyHandler deliveryPolicyHandler = new DeliveryPolicyHandler();

    // ------------------------------------------------------------------
    // 1. 항목 등록: POST /api/v1/cart (1, 2번 요청사항 반영)
    // ------------------------------------------------------------------
    @Transactional
    public CartDto addItemToCart(CartAddItemRequest request) {
        String userId = SecurityUtil.getCurrentUserId();

        // 1) 메뉴 정보 DB 조회
        MenuDetailsDto menuDetails = itemRepository.findMenuDetailsById(request.getMenuId())
                .orElseThrow(() -> new MenuInfoNotFoundException("메뉴(ID: " + request.getMenuId() + ") 정보 없음"));

        // 2) 장바구니 조회 및 가게 충돌 검증
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        Cart cart = existingCart.orElse(null);

        if (cart != null) {
            if (!cart.getStoreId().equals(menuDetails.getStoreId())) {
                throw new CartStoreConflictException(cart.getStoreName(), menuDetails.getStoreName());
            }
        } else {
            // 새 장바구니 생성 (DB 조회 정보를 기반으로 초기화)
            // 💡 Cart 엔티티의 새로운 필드들을 초기화합니다.
            cart = new Cart(
                    userId,
                    menuDetails.getStoreId(),
                    menuDetails.getStoreName(),
                    menuDetails.getDeliveryFee()
                    // 💡 나머지 필드들은 엔티티에서 기본값으로 초기화되거나 DTO에서 가져옵니다.
            );

            // 💡 1, 2. 새 장바구니 생성 시 요청받은 배달 방식 및 요청사항을 반영합니다.
            cart.setIsSingleDelivery(request.getIsSingleDelivery() != null ? request.getIsSingleDelivery() : false);
            cart.setOwnerRequest(request.getOwnerRequest());
            cart.setPartnerRequest(request.getPartnerRequest());

            // 💡 3. 장바구니 생성 시 유저의 현재 기본 주소를 반영합니다.
            String defaultAddress = deliveryPolicyHandler.findDefaultAddressByUsername(userId);
            cart.setDeliveryAddress(defaultAddress);
        }

        // 3) CartItem 생성 및 가격/이름 유효성 검증
        CartItem newItem = createCartItem(request, menuDetails);

        // 4) 기존 항목과 동일한 항목이 있다면 수량만 증가
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> isSameCartItem(item, newItem))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + newItem.getQuantity());
        } else {
            cart.getItems().add(newItem);
        }

        // 5) Redis에 저장
        cartRepository.save(cart);

        // 💡 3. isAblePay 검증을 포함하여 DTO를 반환
        return buildCartDto(cart);
    }

    // ------------------------------------------------------------------
    // 2. 장바구니 조회: GET /api/v1/cart (3번 요청 반영)
    // ------------------------------------------------------------------
    public CartDto getCart() {
        String userId = SecurityUtil.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("장바구니가 비어있습니다."));

        // 💡 3. 조회 시마다 유저의 현재 기본 주소를 가져와서 장바구니에 반영합니다.
        String currentDefaultAddress = deliveryPolicyHandler.findDefaultAddressByUsername(userId);
        cart.setDeliveryAddress(currentDefaultAddress);

        // 💡 Redis에 업데이트된 주소 저장 (다음번 조회나 수정 시 사용)
        cartRepository.save(cart);

        // 💡 3. isAblePay 검증을 포함하여 DTO를 반환
        return buildCartDto(cart);
    }

    // ------------------------------------------------------------------
    // 4. 장바구니 속성 수정: PATCH /api/v1/cart (1, 2번 요청 반영)
    // ------------------------------------------------------------------
    @Transactional
    public CartDto updateCartProperties(CartUpdateRequest request) {
        String userId = SecurityUtil.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("장바구니가 비어있습니다."));

        boolean isModified = false;

        // 💡 1. 배달 방식 수정 (isSingleDelivery)
        if (request.getIsSingleDelivery() != null) {
            cart.setIsSingleDelivery(request.getIsSingleDelivery());
            isModified = true;
        }

        // 💡 2. 요청 사항 수정 (ownerRequest)
        if (request.getOwnerRequest() != null) {
            cart.setOwnerRequest(request.getOwnerRequest());
            isModified = true;
        }

        // 💡 2. 요청 사항 수정 (partnerRequest)
        if (request.getPartnerRequest() != null) {
            cart.setPartnerRequest(request.getPartnerRequest());
            isModified = true;
        }

        if (isModified) {
            // 변경된 내용 Redis에 저장
            cartRepository.save(cart);
        }

        // 💡 3. isAblePay 검증을 포함하여 DTO를 반환
        return buildCartDto(cart);
    }

    // ------------------------------------------------------------------
    // 5. 결제 후 처리 로직
    // ------------------------------------------------------------------
    @Transactional
    public void processPaymentSuccess() {
        String userId = SecurityUtil.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("결제할 장바구니가 없습니다."));

        // 💡 5. 결제 상태 '결제완료'로 변경
        cart.setPayInfo("결제완료");
        cartRepository.save(cart);

        // 💡 5. Order API와 테이블로 데이터 전달 (OrderService 역할)
        deliveryPolicyHandler.sendOrderData(cart);

        // 💡 5. 레디스에서 장바구니 정보 삭제
        cartRepository.deleteByUserId(userId);
    }

    // ------------------------------------------------------------------
    // 헬퍼: CartDto 빌드 및 isAblePay 검증 (3번 요청)
    // ------------------------------------------------------------------
    private CartDto buildCartDto(Cart cart) {
        // 1. DTO 변환
        CartDto cartDto = CartDto.from(cart);

        // 2. 배달 가능 여부 검증
        boolean isAblePay = deliveryPolicyHandler.checkDeliveryAvailability(
                cart.getStoreId(), cart.getDeliveryAddress());

        // 3. isAblePay 설정 후 반환
        return cartDto.toBuilder().isAblePay(isAblePay).build();
    }


    // ------------------------------------------------------------------
    // 기존 헬퍼 메서드 유지
    // ------------------------------------------------------------------
    private CartItem createCartItem(CartAddItemRequest request, MenuDetailsDto menuDetails) {
        CartItem item = new CartItem();
        item.setCartItemId(UUID.nameUUIDFromBytes(String.valueOf(uniqueIdCounter.incrementAndGet()).getBytes()));
        item.setMenuId(menuDetails.getMenuId());
        item.setMenuName(menuDetails.getMenuName());
        item.setMenuFee(menuDetails.getMenuFee());
        item.setQuantity(request.getQuantity());
        // ... (기존 옵션 처리 로직 유지) ...
        if (request.getOptions() != null) {
            for (CartAddItemRequest.OptionDto optReq : request.getOptions()) {

                MenuDetailsDto.OptionValueDto optValueDto = menuDetails.getOptions().stream()
                        .flatMap(g -> g.getValues().stream())
                        .filter(v -> v.getMenuOptValueId().equals(optReq.getMenuOptValueId()))
                        .findFirst()
                        .orElseThrow(() -> new MenuInfoNotFoundException("옵션 값(ID: " + optReq.getMenuOptValueId() + ") 정보 없음"));

                MenuDetailsDto.OptionGroupDto optGroupDto = menuDetails.getOptions().stream()
                        .filter(g -> g.getValues().stream().anyMatch(v -> v.getMenuOptValueId().equals(optReq.getMenuOptValueId())))
                        .findFirst()
                        .orElseThrow(() -> new MenuInfoNotFoundException("옵션 그룹 정보 없음"));

                CartItemOpt opt = new CartItemOpt();
                opt.setCartItemOptId(UUID.nameUUIDFromBytes(String.valueOf(uniqueIdCounter.incrementAndGet()).getBytes()));
                opt.setMenuOptId(optGroupDto.getMenuOptId());
                opt.setMenuOptName(optGroupDto.getMenuOptName());
                opt.setMenuOptValueId(optValueDto.getMenuOptValueId());
                opt.setMenuOptValueName(optValueDto.getValueName());
                opt.setFee(optValueDto.getFee());

                item.getOptions().add(opt);
            }
        }
        return item;
    }

    private boolean isSameCartItem(CartItem item1, CartItem item2) {
        if (!item1.getMenuId().equals(item2.getMenuId()) || item1.getOptions().size() != item2.getOptions().size()) {
            return false;
        }

        List<UUID> ids1 = item1.getOptions().stream().map(CartItemOpt::getMenuOptValueId).sorted().collect(Collectors.toList());
        List<UUID> ids2 = item2.getOptions().stream().map(CartItemOpt::getMenuOptValueId).sorted().collect(Collectors.toList());

        return ids1.equals(ids2);
    }

    // ------------------------------------------------------------------
    // 3. 장바구니 비우기 (기존 로직 유지)
    // ------------------------------------------------------------------
    public void clearCart() {
        String userId = SecurityUtil.getCurrentUserId();
        cartRepository.deleteByUserId(userId);
    }
}