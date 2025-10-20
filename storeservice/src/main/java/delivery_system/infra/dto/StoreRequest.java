package delivery_system.infra.dto;

package delivery_system.infra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JSON 역직렬화를 위해 기본 생성자가 필요합니다.
public class StoreRequest {

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "가게 주소는 필수입니다.")
    private String address; // "경기도 수원시..." -> 서버에서 GeoCoding 할 주소

    @NotBlank(message = "사업자등록번호는 필수입니다.")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자등록번호 형식이 올바르지 않습니다. (예: 123-45-67890)")
    private String businessNumber; // 사업자등록번호

    @NotBlank(message = "가게 전화번호는 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category; // 예: "치킨", "한식", "카페"

    private String description; // 가게 소개 (선택 사항)
}
