package delivery_system.cart.presentation.dto;

import lombok.Getter; // @Data 대신 @Getter, @Setter 사용 권장
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Getter // DTO 필드에 대한 접근자
@Setter // DTO 필드에 대한 설정자
public class CartAddItemRequest {

    // --- 기존 필드 (필수) ---
    @NotNull(message = "메뉴 ID는 필수입니다.")
    private UUID menuId;

    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private int quantity; // 기본 타입(int)은 null을 허용하지 않으므로 @NotNull 불필요

    private List<OptionDto> options; // 옵션은 없을 수도 있으므로 @NotNull 제거

    // --- 1. 배달 방식 추가 ---
    /**
     * 한집배달 선택 여부.
     * null로 들어오면 백엔드 Service에서 기본값(false=일반배달) 처리 가능.
     * 장바구니에 처음 메뉴를 담을 때는 선택 사항이므로 @NotNull 제거.
     */
    private Boolean isSingleDelivery;

    // --- 2. 배달 요청사항 추가 ---
    /**
     * 가게 사장님께 요청사항.
     * 필수 입력이 아니므로 @NotNull 제거.
     */
    private String ownerRequest;

    /**
     * 배달 파트너에게 요청사항.
     * 필수 입력이 아니므로 @NotNull 제거.
     */
    private String partnerRequest;

    @Getter // 내부 DTO도 @Data 대신 명시적으로 설정
    @Setter
    public static class OptionDto {
        @NotNull(message = "메뉴 옵션 값 ID는 필수입니다.")
        private UUID menuOptValueId;
    }
}