package delivery_system.infra.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HideReviewRequest {

    @NotNull(message = "hide는 필수입니다")
    private Boolean hide;
}