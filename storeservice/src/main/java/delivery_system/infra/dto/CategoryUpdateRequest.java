package delivery_system.infra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JSON 처리를 위한 기본 생성자
public class CategoryUpdateRequest {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String categoryName;
}