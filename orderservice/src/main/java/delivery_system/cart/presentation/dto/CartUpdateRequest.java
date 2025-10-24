package delivery_system.cart.presentation.dto;// delivery_system/cart/domain/dto/CartUpdateRequest.java

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateRequest {
    // 1. 배달 방식 수정
    private Boolean isSingleDelivery;

    // 2. 배달 요청사항 수정
    private String ownerRequest;
    private String partnerRequest;

    // 3. 주소 수정 (선택된 주소 ID 등) - 현재는 주소 테이블과의 연동이 복잡하므로,
    //    주소는 Service 로직에서 default 주소를 가져와서 업데이트한다고 가정합니다.
    //    (혹은 특정 주소 ID를 받을 필드를 추가 가능)

    // 4. 아이템 수량 수정 (이 DTO는 전체 Cart의 속성 수정용. 아이템 수정은 별도 엔드포인트가 필요)

    // 5. PayInfo는 결제 시스템에서만 수정한다고 가정하고 DTO에서 제외합니다.
}