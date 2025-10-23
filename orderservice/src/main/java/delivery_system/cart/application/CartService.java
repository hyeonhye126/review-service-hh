package delivery_system.cart.application;

import delivery_system.cart.domain.Entity.Cart; // 💡 import 수정
import delivery_system.cart.domain.Entity.CartItem; // 💡 import 수정
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

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    private final AtomicLong uniqueIdCounter = new AtomicLong(0);

    // ------------------------------------------------------------------
    // 1. 항목 등록: POST /api/v1/cart
    // ------------------------------------------------------------------
    @Transactional
    public CartDto addItemToCart(CartAddItemRequest request) {
        String userId = SecurityUtil.getCurrentUserId(); // 🔐 현재 로그인 사용자 ID

        // 1) 메뉴 정보 DB 조회 (가격 및 이름 검증)
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
            cart = new Cart(
                    userId,
                    menuDetails.getStoreId(),
                    menuDetails.getStoreName(),
                    menuDetails.getDeliveryFee()
            );
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

        return CartDto.from(cart);
    }

    // ------------------------------------------------------------------
    // 2. 장바구니 조회: GET /api/v1/cart
    // ------------------------------------------------------------------
    public CartDto getCart() {
        String userId = SecurityUtil.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("장바구니가 비어있습니다."));

        return CartDto.from(cart);
    }

    // ------------------------------------------------------------------
    // 헬퍼: CartItem 객체 생성 및 유효성 검증
    // ------------------------------------------------------------------
    private CartItem createCartItem(CartAddItemRequest request, MenuDetailsDto menuDetails) {
        CartItem item = new CartItem();
        item.setCartItemId(UUID.nameUUIDFromBytes(String.valueOf(uniqueIdCounter.incrementAndGet()).getBytes()));
        item.setMenuId(menuDetails.getMenuId());
        item.setMenuName(menuDetails.getMenuName());
        item.setMenuFee(menuDetails.getMenuFee());
        item.setQuantity(request.getQuantity());

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
    // 3. 장바구니 비우기
    // ------------------------------------------------------------------
    public void clearCart() {
        String userId = SecurityUtil.getCurrentUserId();
        cartRepository.deleteByUserId(userId);
    }
}