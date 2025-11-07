package delivery_system.review.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResStoreReviewDtoV1 {
    private UUID storeId;
    private Double storeRatingAvg;
    private Long storeReviewCount;
    private List<ResReviewDtoV1> reviews;
}
