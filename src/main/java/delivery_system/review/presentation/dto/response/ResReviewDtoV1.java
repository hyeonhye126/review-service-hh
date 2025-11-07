package delivery_system.review.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResReviewDtoV1 {
    private UUID reviewId;
    private UUID orderId;
    private UUID storeId;
    private String customerId;
    private Short rating;
    private String content;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
