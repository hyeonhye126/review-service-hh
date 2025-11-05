package delivery_system.application.service;

import delivery_system.domain.Review;
import delivery_system.domain.ReviewRepository;
import delivery_system.infra.dto.request.CreateReviewRequest;
import delivery_system.infra.dto.request.UpdateReviewRequest;
import delivery_system.infra.dto.response.ReviewResponse;
import delivery_system.infra.security.SecurityUtil;
import delivery_system.global.exception.review.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 생성
     * - orderId당 하나의 리뷰만 존재 가능
     * - rating: 1~5점
     * - content: 10자 이상 1000자 이하
     * - 주문 고객만 작성 가능
     */
    public ReviewResponse create(UUID orderId, UUID storeId, String customerId,
                                 CreateReviewRequest request) {

        // HttpStatus 제거 - message만 던짐
        if (reviewRepository.existsByOrderIdAndDeletedAtIsNull(orderId)) {
            throw new ReviewException("이미 작성된 리뷰가 존재합니다");
        }

        Review review = Review.create(
                orderId,
                storeId,
                customerId,
                request.getRating(),
                request.getContent()
        );

        Review savedReview = reviewRepository.save(review);

        System.out.println("✅ 리뷰 생성 완료 - reviewId: " + savedReview.getReviewId()
                + ", orderId: " + orderId + ", customerId: " + customerId);

        return toResponse(savedReview);
    }

    /**
     * 특정 주문의 리뷰 조회
     */
    public ReviewResponse getByOrder(UUID orderId) {
        Review review = reviewRepository
                .findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ReviewException("리뷰를 찾을 수 없습니다"));

        return toResponse(review);
    }

    /**
     * 특정 가게의 리뷰 페이징 조회
     * - 삭제되지 않은 리뷰만 조회
     * - 최신순 정렬
     */
    public Page<ReviewResponse> listByStore(UUID storeId, Pageable pageable) {
        Page<Review> reviews = reviewRepository
                .findByStoreIdAndDeletedAtIsNull(storeId, pageable);

        return reviews.map(this::toResponse);
    }

    /**
     * 특정 고객의 리뷰 페이징 조회 (인증 필요)
     */
    public Page<ReviewResponse> listByCustomer(String customerId, Pageable pageable) {
        Page<Review> reviews = reviewRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId, pageable);

        return reviews.map(this::toResponse);
    }

    /**
     * 리뷰 수정 (인증 필요)
     * - 작성자만 수정 가능
     * - 소프트 델리트된 리뷰는 수정 불가
     */
    public ReviewResponse update(UUID reviewId, String customerId,
                                 UpdateReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("리뷰를 찾을 수 없습니다"));

        if (review.getDeletedAt() != null) {
            throw new ReviewException("삭제된 리뷰는 수정할 수 없습니다");
        }

        if (!review.getCustomerId().equals(customerId)) {
            throw new ReviewException("수정 권한이 없습니다");
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());
        review.setUpdatedBy(customerId);

        Review updatedReview = reviewRepository.save(review);

        System.out.println("✏️ 리뷰 수정 완료 - reviewId: " + reviewId);

        return toResponse(updatedReview);
    }

    /**
     * 리뷰 소프트 삭제 (인증 필요)
     * - CUSTOMER, MANAGER, MASTER role만 가능
     * - 작성자 확인 (CUSTOMER인 경우)
     * - deleted_at, deleted_by 저장
     *
     * @throws ReviewException 권한이 없거나 리뷰를 찾을 수 없을 때
     */
    public void softDelete(UUID reviewId, String customerId) {

        String userRole = SecurityUtil.getCurrentUserRole();

        if (userRole == null ||
                (!userRole.equals("CUSTOMER") &&
                        !userRole.equals("MANAGER") &&
                        !userRole.equals("MASTER"))) {
            throw new ReviewException("리뷰 삭제 권한이 없습니다");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("리뷰를 찾을 수 없습니다"));

        if (userRole.equals("CUSTOMER") && !review.getCustomerId().equals(customerId)) {
            throw new ReviewException("리뷰 삭제 권한이 없습니다");
        }

        if (review.getDeletedAt() != null) {
            throw new ReviewException("이미 삭제된 리뷰입니다");
        }

        review.setDeletedAt(LocalDateTime.now());
        review.setDeletedBy(customerId);
        reviewRepository.save(review);

        System.out.println("🗑️ 리뷰 삭제 완료 - reviewId: " + reviewId + ", role: " + userRole);
    }

    /**
     * 리뷰 노출/숨김 처리 (관리자용)
     * - 관리자만 가능
     * - 실제 삭제가 아닌 가시성 제어
     * - TODO: DB 스키마에 is_hidden 컬럼 추가 필요
     */
//    public ReviewResponse hide(UUID reviewId, Boolean hide, String adminId) {
//
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new ReviewException("리뷰를 찾을 수 없습니다"));
//
//        // is_hidden 컬럼이 필요한 경우 추후 구현
//        // review.setIsHidden(hide);
//        review.setUpdatedAt(LocalDateTime.now());
//        review.setUpdatedBy(adminId);
//
//        Review updatedReview = reviewRepository.save(review);
//        return toResponse(updatedReview);
//    }

    /**
     * 엔티티를 응답 DTO로 변환
     */
    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getStoreId(),
                review.getCustomerId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getOrderId()
        );
    }
}