package delivery_system.store.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResCreateStoreDtoV1 {
    private UUID storeId; //가게id
    private String ownerId; //
    private String storeName; //가게명
    private String description; //가게 소개
    private String storeAddress; //주소 원문
    private Double storeRatingAvg; // 가게 평점
    private Integer storeReviewCount; // 리뷰 수
    private LocalDateTime createdAt;
}
