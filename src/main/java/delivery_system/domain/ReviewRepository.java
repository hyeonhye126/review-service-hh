package delivery_system.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * 특정 주문의 리뷰 조회 (삭제되지 않은 리뷰만)
     */
    Optional<Review> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    /**
     * 특정 고객의 리뷰 조회 (삭제되지 않은 리뷰만)
     */
    Page<Review> findByCustomerIdAndDeletedAtIsNull(
            String customerId, Pageable pageable);

    /**
     * 특정 가게의 리뷰 조회 (삭제되지 않은 리뷰만)
     * 조건: deleted_at IS NULL
     */
    @Query("SELECT r FROM Review r WHERE r.storeId = :storeId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Review> findByStoreIdAndDeletedAtIsNull(
            @Param("storeId") UUID storeId, Pageable pageable);

    /**
     * 같은 주문으로 이미 리뷰를 작성했는지 확인
     */
    boolean existsByOrderIdAndDeletedAtIsNull(UUID orderId);
}