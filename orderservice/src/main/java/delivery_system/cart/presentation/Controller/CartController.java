package delivery_system.cart.presentation.Controller;

import delivery_system.cart.application.CartService;
import delivery_system.cart.presentation.dto.CartAddItemRequest;
import delivery_system.cart.presentation.dto.CartDto;
import delivery_system.cart.presentation.dto.CartUpdateRequest; // 💡 새로운 DTO import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // 1. 항목 등록/추가: POST /api/v1/cart (기존 로직 유지)
    @PostMapping
    public ResponseEntity<CartDto> addItemToCart(@RequestBody CartAddItemRequest request) {
        CartDto cartDto = cartService.addItemToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    // 2. 장바구니 조회: GET /api/v1/cart (기존 로직 유지)
    // 💡 GET 요청 시 Service에서 주소 검증 및 isAblePay 설정 로직이 실행됩니다.
    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        CartDto cartDto = cartService.getCart();
        return ResponseEntity.ok(cartDto);
    }

    // ------------------------------------------------------------------
    // 4. 장바구니 속성 수정: PATCH /api/v1/cart (추가)
    // ------------------------------------------------------------------
    /**
     * 장바구니 컨테이너의 속성(배달 방식, 요청사항 등)을 수정합니다.
     */
    @PatchMapping
    public ResponseEntity<CartDto> updateCartProperties(@RequestBody CartUpdateRequest request) {
        // Service에서 수정 로직 처리 후, 주소 검증이 완료된 최신 DTO를 반환
        CartDto updatedCart = cartService.updateCartProperties(request);
        return ResponseEntity.ok(updatedCart);
    }

    // ------------------------------------------------------------------
    // 5. 결제 후 처리: POST /api/v1/cart/checkout/success (추가)
    // ------------------------------------------------------------------
    /**
     * 결제 서비스로부터 결제 성공 알림을 받거나, 최종 주문 API 호출을 처리합니다.
     */
    @PostMapping("/checkout/success")
    public ResponseEntity<Void> handlePaymentSuccess() {
        // Service에서 Redis 데이터 삭제 및 Order API 전송 로직 수행
        cartService.processPaymentSuccess();
        return ResponseEntity.ok().build();
    }


    // 3. 장바구니 비우기: DELETE /api/v1/cart (기존 로직 유지)
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    // 4. 항목 수량 변경/삭제 등의 나머지 API는 필요에 따라 구현합니다.
    // (예: PATCH /api/v1/cart/items/{cartItemId} 로직은 현재 구현에서 생략됨)
}