package delivery_system.global.exception;

import delivery_system.global.exception.review.ReviewException;
import delivery_system.global.presentation.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 전역 예외 처리 Advice
 * 모든 예외를 BaseResponse 형식으로 통일하고 HttpStatus 결정
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * ReviewException 처리
     * 모든 리뷰 관련 오류를 BAD_REQUEST로 처리
     */
    @ExceptionHandler(ReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // ✅ 여기서 HttpStatus 결정
    public BaseResponse<Void> handleReviewException(ReviewException e) {
        System.err.println("❌ ReviewException 발생: " + e.getMessage());
        return BaseResponse.onFailure("REVIEW_ERROR", e.getMessage(), null);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        System.err.println("❌ IllegalArgumentException 발생: " + e.getMessage());
        return BaseResponse.onFailure("BAD_REQUEST", e.getMessage(), null);
    }

    /**
     * @Valid 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "입력값이 유효하지 않습니다";

        System.err.println("❌ Validation 실패: " + message);
        return BaseResponse.onFailure("VALIDATION_ERROR", message, null);
    }

    /**
     * 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Void> handleGeneralException(Exception e) {
        System.err.println("❌ 예상치 못한 오류: " + e.getMessage());
        e.printStackTrace();
        return BaseResponse.onFailure("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다", null);
    }
}