package delivery_system.infra.controller;

import delivery_system.infra.dto.request.CreateReviewRequest;
import delivery_system.infra.dto.request.UpdateReviewRequest;
import delivery_system.infra.dto.request.HideReviewRequest;
import delivery_system.infra.dto.response.ReviewResponse;
import delivery_system.infra.service.ReviewService;
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
     * POST /api/v1/reviews/{orderId}
     *
     * 요청 헤더: X-USER-ID (고객 ID)
     * 경로 변수: storeId (가게 ID), orderId (주문 ID)
     * 요청 바디: CreateReviewRequest
     */
    @PostMapping("/{storeId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateReviewRequest req,
            @RequestHeader("X-USER-ID") String userId) {
        return reviewService.create(orderId, storeId, userId, req);
    }

    /**
     * 특정 가게의 리뷰 목록 조회 (페이징)
     * GET /api/v1/reviews/store/{storeId}?page=1&size=10
     *
     * 페이지: 1부터 시작 (내부에서 0으로 변환)
     */
    @GetMapping("/store/{storeId}")
    public Page<ReviewResponse> listByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reviewService.listByStore(storeId, PageRequest.of(Math.max(0, page - 1), size));
    }

    /**
     * 특정 주문의 리뷰 조회
     * GET /api/v1/reviews/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ReviewResponse getByOrder(@PathVariable UUID orderId) {
        return reviewService.getByOrder(orderId);
    }

    /**
     * 특정 고객의 리뷰 목록 조회 (페이징)
     * GET /api/v1/reviews/customer?page=1&size=10
     *
     * 요청 헤더: X-USER-ID (고객 ID)
     */
    @GetMapping
    public Page<ReviewResponse> listByCustomer(
            @RequestHeader("X-USER-ID") String customerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reviewService.listByCustomer(customerId, PageRequest.of(Math.max(0, page - 1), size));
    }

    /**
     * 리뷰 수정
     * PUT /api/v1/reviews/{reviewId}
     *
     * 요청 헤더: X-USER-ID (고객 ID)
     * 경로 변수: reviewId (리뷰 ID)
     * 요청 바디: UpdateReviewRequest
     */
    @PutMapping("/{reviewId}")
    public ReviewResponse update(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest req,
            @RequestHeader("X-USER-ID") String userId) {
        return reviewService.update(reviewId, userId, req);
    }

    /**
     * 리뷰 삭제 (소프트 삭제)
     * DELETE /api/v1/reviews/{reviewId}
     *
     * 요청 헤더: X-USER-ID (고객 ID)
     * 경로 변수: reviewId (리뷰 ID)
     */
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID reviewId,
            @RequestHeader("X-USER-ID") String userId) {
        reviewService.softDelete(reviewId, userId);
    }

    /**
     * 리뷰 숨김/노출 처리 (관리자용)
     * POST /api/v1/reviews/admin/{reviewId}/hide
     *
     * 요청 헤더: X-ADMIN-ID (관리자 ID)
     * 경로 변수: reviewId (리뷰 ID)
     * 요청 바디: HideReviewRequest { "hide": true/false }
     */
//    @PostMapping("/admin/{reviewId}/hide")
//    public ReviewResponse hide(
//            @PathVariable UUID reviewId,
//            @Valid @RequestBody HideReviewRequest req,
//            @RequestHeader("X-ADMIN-ID") String adminId) {
//        return reviewService.hide(reviewId, req.getHide(), adminId);
//    }
}