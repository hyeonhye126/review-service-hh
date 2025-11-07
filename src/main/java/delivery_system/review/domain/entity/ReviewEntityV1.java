package delivery_system.review.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Data
public class ReviewEntityV1 {
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
}
