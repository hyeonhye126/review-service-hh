package delivery_system.cart.exception;

// 장바구니를 찾을 수 없을 때 발생하는 예외
public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }
}