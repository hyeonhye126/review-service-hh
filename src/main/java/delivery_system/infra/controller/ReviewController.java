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
    public ReviewResponse create(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateReviewRequest req) {

        // 경로 매개변수 검증
        if (storeId == null || orderId == null) {
            throw new IllegalArgumentException("storeId와 orderId는 필수입니다");
        }

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("📝 리뷰 생성 요청 - userId: " + userId
                + ", storeId: " + storeId + ", orderId: " + orderId);

        return reviewService.create(orderId, storeId, userId, req);
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
    public Page<ReviewResponse> listByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (storeId == null) {
            throw new IllegalArgumentException("storeId는 필수입니다");
        }

        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;

        System.out.println("📖 가게 리뷰 목록 조회 - storeId: " + storeId
                + ", page: " + page + ", size: " + size);

        return reviewService.listByStore(storeId, PageRequest.of(page - 1, size));
    }

    /**
     * 특정 주문의 리뷰 조회 (공개 API)
     * GET /api/v1/reviews/order/{orderId}
     *
     * @param orderId 주문 ID
     * @return 리뷰 정보
     */
    @GetMapping("/order/{orderId}")
    public ReviewResponse getByOrder(@PathVariable UUID orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("orderId는 필수입니다");
        }

        System.out.println("📖 주문별 리뷰 조회 - orderId: " + orderId);

        return reviewService.getByOrder(orderId);
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
    public Page<ReviewResponse> listMyReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        String customerId = SecurityUtil.getCurrentUserId();

        if (customerId == null) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다");
        }

        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;

        System.out.println("📖 내 리뷰 목록 조회 - customerId: " + customerId
                + ", page: " + page + ", size: " + size);

        return reviewService.listByCustomer(customerId, PageRequest.of(page - 1, size));
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
    public ReviewResponse update(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest req) {

        if (reviewId == null) {
            throw new IllegalArgumentException("reviewId는 필수입니다");
        }

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("✏️ 리뷰 수정 요청 - userId: " + userId + ", reviewId: " + reviewId);

        return reviewService.update(reviewId, userId, req);
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
    public void delete(@PathVariable UUID reviewId) {

        if (reviewId == null) {
            throw new IllegalArgumentException("reviewId는 필수입니다");
        }

        String userId = SecurityUtil.getCurrentUserId();

        System.out.println("🗑️ 리뷰 삭제 - userId: " + userId + ", reviewId: " + reviewId);

        reviewService.softDelete(reviewId, userId);
    }
}