package delivery_system.global.infra.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Spring이 JSON을 객체로 변환할 때 기본 생성자가 필요합니다.
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 이름은 필수입니다.") // ⬅️ [필수] 비어있는 값이 들어오지 않도록 검증
    private String category_name;

}