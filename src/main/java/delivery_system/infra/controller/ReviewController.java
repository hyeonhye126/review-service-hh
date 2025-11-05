package delivery_system.infra.controller;

import delivery_system.global.presentation.response.BaseResponse;
import delivery_system.infra.dto.request.CreateReviewRequest;
import delivery_system.infra.dto.request.UpdateReviewRequest;
import delivery_system.infra.dto.response.ReviewResponse;
import delivery_system.infra.security.SecurityUtil;
import delivery_system.application.service.ReviewService;
import delivery_system.global.exception.review.ReviewException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성 (인증 필수)
     * POST /api/v1/reviews/{storeId}/orders/{orderId}
     *
     * @param storeId 가게 ID
     * @param orderId 주문 ID
     * @param req 리뷰 요청 데이터 (rating, content)
     * @return 생성된 리뷰 정보
     */
    @PostMapping("/{storeId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<ReviewResponse> create(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateReviewRequest req) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("📝 리뷰 생성 요청 - userId: " + userId
                + ", storeId: " + storeId + ", orderId: " + orderId);

        ReviewResponse review = reviewService.create(orderId, storeId, userId, req);

        return BaseResponse.onSuccess(review);
    }

    /**
     * 특정 가게의 리뷰 목록 조회 (공개 API)
     * GET /api/v1/reviews/store/{storeId}
     *
     * @param storeId 가게 ID
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @return 리뷰 페이지
     */
    @GetMapping("/store/{storeId}")
    public BaseResponse<Page<ReviewResponse>> listByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;

        System.out.println("📖 가게 리뷰 목록 조회 - storeId: " + storeId
                + ", page: " + page + ", size: " + size);

        Page<ReviewResponse> list = reviewService.listByStore(storeId, PageRequest.of(page - 1, size));

        return BaseResponse.onSuccess(list);
    }

    /**
     * 특정 주문의 리뷰 조회 (공개 API)
     * GET /api/v1/reviews/order/{orderId}
     *
     * @param orderId 주문 ID
     * @return 리뷰 정보
     */
    @GetMapping("/order/{orderId}")
    public BaseResponse<ReviewResponse> getByOrder(@PathVariable UUID orderId) {

        System.out.println("📖 주문별 리뷰 조회 - orderId: " + orderId);

        ReviewResponse review = reviewService.getByOrder(orderId);

        return BaseResponse.onSuccess(review);
    }

    /**
     * 내 리뷰 목록 조회 (인증 필요)
     * GET /api/v1/reviews
     *
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @return 내 리뷰 페이지
     */
    @GetMapping
    public BaseResponse<Page<ReviewResponse>> listMyReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        String customerId = SecurityUtil.getCurrentUserId();

        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;

        System.out.println("📖 내 리뷰 목록 조회 - customerId: " + customerId
                + ", page: " + page + ", size: " + size);

        Page<ReviewResponse> reviews = reviewService.listByCustomer(customerId, PageRequest.of(page - 1, size));

        return BaseResponse.onSuccess(reviews);
    }

    /**
     * 리뷰 수정 (인증 필수, 작성자만 가능)
     * PUT /api/v1/reviews/{reviewId}
     *
     * @param reviewId 리뷰 ID
     * @param req 수정 요청 데이터 (rating, content)
     * @return 수정된 리뷰 정보
     */
    @PutMapping("/{reviewId}")
    public BaseResponse<ReviewResponse> update(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest req) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("✏️ 리뷰 수정 요청 - userId: " + userId + ", reviewId: " + reviewId);

        ReviewResponse review = reviewService.update(reviewId, userId, req);

        return BaseResponse.onSuccess(review);
    }

    /**
     * 리뷰 삭제 (인증 필요)
     * DELETE /api/v1/reviews/{reviewId}
     * 권한: CUSTOMER (자신의 리뷰만), MANAGER, MASTER (모든 리뷰)
     *
     * @param reviewId 리뷰 ID
     */
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BaseResponse<Void> delete(@PathVariable UUID reviewId) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("🗑️ 리뷰 삭제 - userId: " + userId + ", reviewId: " + reviewId);

        reviewService.softDelete(reviewId, userId);

        return BaseResponse.onSuccess(null);
    }
}