package delivery_system.cart.presentation.Controller;

import delivery_system.cart.application.CartService;
import delivery_system.cart.presentation.dto.CartAddItemRequest;
import delivery_system.cart.presentation.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // 1. 항목 등록/추가: POST /api/v1/cart
    @PostMapping
    public ResponseEntity<CartDto> addItemToCart(@RequestBody CartAddItemRequest request) {
        CartDto cartDto = cartService.addItemToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    // 2. 장바구니 조회: GET /api/v1/cart
    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        CartDto cartDto = cartService.getCart();
        return ResponseEntity.ok(cartDto);
    }

    // 3. 장바구니 비우기: DELETE /api/v1/cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    // 4. 항목 수량 변경/삭제 등의 나머지 API는 필요에 따라 구현합니다.

}