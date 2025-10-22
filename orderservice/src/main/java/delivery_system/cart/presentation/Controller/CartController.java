package delivery_system.cart.presentation.Controller;

import delivery_system.cart.application.CartService;
import delivery_system.cart.presentation.dto.CartAddItemRequest;
import delivery_system.cart.presentation.dto.CartDto;
import delivery_system.cart.presentation.dto.CartItemOptAddRequest;
import delivery_system.cart.presentation.dto.CartItemQuantityRequest;
import delivery_system.cart.presentation.dto.CartItemOptUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // TODO: 실제 구현 시 Spring Security Context에서 userId를 가져와야 합니다.
    private String getCurrentUserId() {
        // 임시로 하드코딩된 사용자 ID 반환 (실제는 인증 로직 필요)
        return "logged_in_user_id";
    }

    // ------------------------------------------------------------------
    // 1. 항목 등록: POST /api/v1/cart
    // ------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<CartDto> addItemToCart(@Valid @RequestBody CartAddItemRequest request) {
        String userId = getCurrentUserId();
        CartDto cartDto = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(cartDto);
    }

    // ------------------------------------------------------------------
    // 2. 항목 수정: PATCH /api/v1/cart/items/{cart_item_id}
    // ------------------------------------------------------------------
    @PatchMapping("/items/{cart_item_id}")
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @PathVariable("cart_item_id") UUID cartItemId,
            @Valid @RequestBody CartItemQuantityRequest request) {

        String userId = getCurrentUserId();
        CartDto cartDto = cartService.updateItemQuantity(userId, cartItemId, request.getCartItemQuantity());
        return ResponseEntity.ok(cartDto);
    }

    // ------------------------------------------------------------------
    // 3. 항목 삭제: DELETE /api/v1/cart/items/{cart_item_id}
    // ------------------------------------------------------------------
    @DeleteMapping("/items/{cart_item_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PathVariable("cart_item_id") UUID cartItemId) {
        String userId = getCurrentUserId();
        cartService.deleteCartItem(userId, cartItemId);
    }

    // ------------------------------------------------------------------
    // 4. 항목 옵션 추가: POST /api/v1/cart/items/{cart_item_id}/opts
    // ------------------------------------------------------------------
    @PostMapping("/items/{cart_item_id}/opts")
    public ResponseEntity<CartDto> addCartItemOptions(
            @PathVariable("cart_item_id") UUID cartItemId,
            @Valid @RequestBody CartItemOptAddRequest request) {

        String userId = getCurrentUserId();
        CartDto cartDto = cartService.addCartItemOptions(userId, cartItemId, request);
        return ResponseEntity.ok(cartDto);
    }

    // ------------------------------------------------------------------
    // 5. 항목 옵션 수정: PATCH /api/v1/cart/items/{cart_item_id}/opts/{cart_item_opt_id}
    // ------------------------------------------------------------------
    @PatchMapping("/items/{cart_item_id}/opts/{cart_item_opt_id}")
    public ResponseEntity<CartDto> updateCartItemOption(
            @PathVariable("cart_item_id") UUID cartItemId,
            @PathVariable("cart_item_opt_id") UUID cartItemOptId,
            @Valid @RequestBody CartItemOptUpdateRequest request) {

        String userId = getCurrentUserId();
        CartDto cartDto = cartService.updateCartItemOption(userId, cartItemId, cartItemOptId, request.getMenuOptValueId());
        return ResponseEntity.ok(cartDto);
    }

    // ------------------------------------------------------------------
    // 6. 항목 옵션 삭제: DELETE /api/v1/cart/items/{cart_item_id}/opts/{cart_item_opt_id}
    // ------------------------------------------------------------------
    @DeleteMapping("/items/{cart_item_id}/opts/{cart_item_opt_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItemOption(
            @PathVariable("cart_item_id") UUID cartItemId,
            @PathVariable("cart_item_opt_id") UUID cartItemOptId) {

        String userId = getCurrentUserId();
        cartService.deleteCartItemOption(userId, cartItemId, cartItemOptId);
    }
}