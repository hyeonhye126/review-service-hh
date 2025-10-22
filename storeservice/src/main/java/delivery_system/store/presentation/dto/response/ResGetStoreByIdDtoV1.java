package delivery_system.store.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResGetStoreByIdDtoV1 {
    private UUID storeId; // 가게 식별자
    private String ownerId; // 점주 식별자
    private String storeName; // 가게명
    private String description; // 가게 소개
    private Integer deliveryFee; // 배달비
    private String storeAddress; // 주소 원문
    private String storeGeom; // 주소 좌표
    private DecimalFormat storeRatingAvg; // 평균 평점
    private Integer StoreReviewCount; // 리뷰 갯수
}
