package delivery_system.infra.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @NotNull(message = "rating은 필수입니다")
    @Min(value = 1, message = "최소 1점이어야 합니다")
    @Max(value = 5, message = "최대 5점입니다")
    private Short rating;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, max = 1000, message = "10자 이상 1000자 이하여야 합니다")
    private String content;
}