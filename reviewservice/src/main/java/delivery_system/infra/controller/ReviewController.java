package delivery_system.infra.controller;

import delivery_system.infra.dto.request.CreateReviewRequest;
import delivery_system.infra.dto.request.UpdateReviewRequest;
import delivery_system.infra.dto.response.ReviewResponse;
import delivery_system.infra.service.ReviewService;
import delivery_system.infra.security.SecurityUtil;
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
     * 리뷰 생성
     * POST /api/v1/reviews/{storeId}/orders/{orderId}
     */
    @PostMapping("/{storeId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateReviewRequest req) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("📝 리뷰 생성 - userId: " + userId + ", storeId: " + storeId + ", orderId: " + orderId);
        return reviewService.create(orderId, storeId, userId, req);
    }

    /**
     * 특정 가게의 리뷰 목록 조회 (공개 API)
     * GET /api/v1/reviews/store/{storeId}
     */
    @GetMapping("/store/{storeId}")
    public Page<ReviewResponse> listByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("📖 리뷰 목록 조회 - storeId: " + storeId);
        return reviewService.listByStore(storeId, PageRequest.of(Math.max(0, page - 1), size));
    }

    /**
     * 특정 주문의 리뷰 조회 (공개 API)
     * GET /api/v1/reviews/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ReviewResponse getByOrder(@PathVariable UUID orderId) {
        System.out.println("📖 주문별 리뷰 조회 - orderId: " + orderId);
        return reviewService.getByOrder(orderId);
    }

    /**
     * 내 리뷰 목록 조회 (인증 필요)
     * GET /api/v1/reviews
     */
    @GetMapping
    public Page<ReviewResponse> listMyReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        String customerId = SecurityUtil.getCurrentUserId();

        System.out.println("📖 내 리뷰 목록 조회 - customerId: " + customerId);
        return reviewService.listByCustomer(customerId, PageRequest.of(Math.max(0, page - 1), size));
    }

    /**
     * 리뷰 수정 (인증 필요)
     * PUT /api/v1/reviews/{reviewId}
     */
    @PutMapping("/{reviewId}")
    public ReviewResponse update(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest req) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("✏️ 리뷰 수정 - userId: " + userId + ", reviewId: " + reviewId);
        return reviewService.update(reviewId, userId, req);
    }

    /**
     * 리뷰 삭제 (인증 필요)
     * DELETE /api/v1/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID reviewId) {

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("🗑️ 리뷰 삭제 - userId: " + userId + ", reviewId: " + reviewId);
        reviewService.softDelete(reviewId, userId);
    }
}