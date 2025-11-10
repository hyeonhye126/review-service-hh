package delivery_system.common.exception;

import delivery_system.common.exception.review.ReviewException;
import delivery_system.common.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 Advice
 * 모든 예외를 BaseResponse 형식으로 통일하고 HttpStatus 결정
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    // 로그인 실패
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<BaseResponse<Object>> onLoginFail(ReviewException e) {
        BaseResponse<Object> response = BaseResponse.onFailure(
                "REVIEW_FAIL",
                e.getMessage(),
                null // 에러 발생 시 데이터(result)는 null로 설정 가능
        );

        // HTTP 상태 코드 404 NOT FOUND와 함께 응답
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}