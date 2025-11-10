package delivery_system.common.exception.review;

/**
 * 리뷰 관련 예외 클래스
 *
 * 사용 예:
 * throw new ReviewException("리뷰를 찾을 수 없습니다");
 */
public class ReviewException extends RuntimeException {

    public ReviewException(String message) {
        super(message);
    }
}