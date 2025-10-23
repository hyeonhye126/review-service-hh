package delivery_system.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID reviewId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "rating", nullable = false)
    private Short rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

//    @Column(name = "is_hidden")
//    private Boolean isHidden;

    public static Review create(UUID orderId, UUID storeId, String customerId,
                                Short rating, String content) {
        Review review = new Review();
        review.reviewId = UUID.randomUUID();
        review.orderId = orderId;
        review.storeId = storeId;
        review.customerId = customerId;
        review.rating = rating;
        review.content = content;
        review.createdAt = LocalDateTime.now();
//        review.isHidden = false;
        return review;
    }
}