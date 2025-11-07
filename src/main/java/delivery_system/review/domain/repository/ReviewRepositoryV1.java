package delivery_system.review.domain.repository;

import delivery_system.review.domain.entity.ReviewEntityV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepositoryV1 extends JpaRepository<ReviewEntityV1, UUID> {
    List<ReviewEntityV1> findAllByStoreIdAndDeletedAtIsNull(UUID storeId);
    ReviewEntityV1 findByOrderIdAndCustomerIdAndDeletedAtIsNull(UUID orderId, String customerId);
    List<ReviewEntityV1> findAllByCustomerIdAndDeletedAtIsNull(String customerId);
    ReviewEntityV1 findByReviewIdAndDeletedAtIsNull(UUID reviewId);
}
