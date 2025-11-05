package delivery_system.global.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모든 API 응답을 통일하는 래퍼 클래스
 *
 * 응답 예시:
 * {
 *   "timestamp": "2025-11-05T15:30:45.123456",
 *   "status": "SUCCESS",
 *   "message": "통신에 성공하였습니다.",
 *   "result": {...}
 * }
 */
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timestamp", "status", "message", "result"})
public class BaseResponse<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    /**
     * 성공 응답 생성
     *
     * 사용 예:
     * return BaseResponse.onSuccess(review);
     * return BaseResponse.onSuccess(reviewList);
     */
    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>("SUCCESS", "통신에 성공하였습니다.", result);
    }

    /**
     * 실패 응답 생성
     *
     * 사용 예:
     * return BaseResponse.onFailure("NOT_FOUND", "리뷰를 찾을 수 없습니다.", null);
     * return BaseResponse.onFailure("BAD_REQUEST", "입력값이 유효하지 않습니다.", null);
     */
    public static <T> BaseResponse<T> onFailure(String status, String message, T data) {
        return new BaseResponse<>(status, message, data);
    }

}