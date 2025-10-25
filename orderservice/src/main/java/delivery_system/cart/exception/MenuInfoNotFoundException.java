package delivery_system.cart.exception;

// 메뉴 또는 옵션 정보가 DB에 없을 때 발생하는 예외
public class MenuInfoNotFoundException extends RuntimeException {

    public MenuInfoNotFoundException(String message) {
        super(message);
    }
}