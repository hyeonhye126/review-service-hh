package delivery_system.cart.exception;

// 다른 가게의 메뉴를 담으려 할 때 발생하는 예외
public class CartStoreConflictException extends RuntimeException {

    public CartStoreConflictException(String existingStoreName, String newStoreName) {
        super(String.format("장바구니에 이미 %s 메뉴가 담겨 있습니다. %s 메뉴를 추가할 수 없습니다.",
                existingStoreName, newStoreName));
    }
}