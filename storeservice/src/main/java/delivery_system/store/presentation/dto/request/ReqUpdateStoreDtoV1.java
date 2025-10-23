package delivery_system.store.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateStoreDtoV1 {
    @JsonProperty("store_name")
    @NotBlank(message = "가게명은 필수입니다.")
    private String storeName; // 가게명

    @JsonProperty("owner_id")
    @NotBlank(message = "소유자 ID는 필수입니다.")
    private String ownerId; //

    @JsonProperty("description")
    @NotBlank(message = "가게 소개는 필수입니다.")
    private String description; // 가게 소개

    @JsonProperty("store_address")
    @NotBlank(message = "가게 주소는 필수입니다.")
    private String storeAddress; // 주소 원문

    @JsonProperty("store_rating_avg")
    private Double storeRatingAvg; // 가게 평점

    @JsonProperty("store_review_count")
    private Integer storeReviewCount; // 리뷰 수
}
