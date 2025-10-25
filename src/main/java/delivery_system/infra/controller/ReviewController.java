package delivery_system.infra.controller;

import delivery_system.infra.dto.request.CreateReviewRequest;
import delivery_system.infra.dto.request.UpdateReviewRequest;
//import delivery_system.infra.dto.request.HideReviewRequest;
import delivery_system.infra.dto.response.ReviewResponse;
import delivery_system.infra.service.ReviewService;
import delivery_system.infra.security.JwtUtil;  // ✅ 추가
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
    private final JwtUtil jwtUtil;  // ✅ 추가

    /**
     * 리뷰 생성
     * POST /api/v1/reviews/{orderId}
     *
     * 요청 헤더: Authorization: Bearer <token> (JWT 필수)
     * 경로 변수: storeId (가게 ID), orderId (주문 ID)
     * 요청 바디: CreateReviewRequest
     */
    @PostMapping("/{storeId}/orders/{orderId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody CreateReviewRequest req,
            @RequestHeader("Authorization") String authHeader) {  // ✅ 변경

        // ✅ JWT에서 userId 추출
        String token = authHeader.substring(7);  // "Bearer " 제거
        String userId = jwtUtil.getUserIdFromToken(token);

        System.out.println("📝 리뷰 생성 - userId: " + userId + ", storeId: " + storeId + ", orderId: " + orderId);
        return reviewService.create(orderId, storeId, userId, req);
    }

    /**
     * 특정 가게의 리뷰 목록 조회 (페이징)
     * GET /api/v1/reviews/store/{storeId}?page=1&size=10
     *
     * 페이지: 1부터 시작 (내부에서 0으로 변환)
     * JWT 불필요 (공개 API)
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
     * 특정 주문의 리뷰 조회
     * GET /api/v1/reviews/order/{orderId}
     *
     * JWT 불필요 (공개 API)
     */
    @GetMapping("/order/{orderId}")
    public ReviewResponse getByOrder(@PathVariable UUID orderId) {
        System.out.println("📖 주문별 리뷰 조회 - orderId: " + orderId);
        return reviewService.getByOrder(orderId);
    }

    /**
     * 특정 고객의 리뷰 목록 조회 (페이징)
     * GET /api/v1/reviews/customer?page=1&size=10
     *
     * 요청 헤더: Authorization: Bearer <token> (JWT 필수)
     */
    @GetMapping
    public Page<ReviewResponse> listByCustomer(
            @RequestHeader("Authorization") String authHeader,  // ✅ 변경
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // ✅ JWT에서 customerId 추출
        String token = authHeader.substring(7);  // "Bearer " 제거
        String customerId = jwtUtil.getUserIdFromToken(token);

        System.out.println("📖 고객별 리뷰 목록 조회 - customerId: " + customerId);
        return reviewService.listByCustomer(customerId, PageRequest.of(Math.max(0, page - 1), size));
    }

    /**
     * 리뷰 수정
     * PUT /api/v1/reviews/{reviewId}
     *
     * 요청 헤더: Authorization: Bearer <token> (JWT 필수)
     * 경로 변수: reviewId (리뷰 ID)
     * 요청 바디: UpdateReviewRequest
     */
    @PutMapping("/{reviewId}")
    public ReviewResponse update(
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateReviewRequest req,
            @RequestHeader("Authorization") String authHeader) {  // ✅ 변경

        // ✅ JWT에서 userId 추출
        String token = authHeader.substring(7);  // "Bearer " 제거
        String userId = jwtUtil.getUserIdFromToken(token);

        System.out.println("✏️ 리뷰 수정 - userId: " + userId + ", reviewId: " + reviewId);
        return reviewService.update(reviewId, userId, req);
    }

    /**
     * 리뷰 삭제 (소프트 삭제)
     * DELETE /api/v1/reviews/{reviewId}
     *
     * 요청 헤더: Authorization: Bearer <token> (JWT 필수)
     * 경로 변수: reviewId (리뷰 ID)
     */
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID reviewId,
            @RequestHeader("Authorization") String authHeader) {  // ✅ 변경

        // ✅ JWT에서 userId 추출
        String token = authHeader.substring(7);  // "Bearer " 제거
        String userId = jwtUtil.getUserIdFromToken(token);

        System.out.println("🗑️ 리뷰 삭제 - userId: " + userId + ", reviewId: " + reviewId);
        reviewService.softDelete(reviewId, userId);
    }

    /**
     * 리뷰 숨김/노출 처리 (관리자용)
     * POST /api/v1/reviews/admin/{reviewId}/hide
     *
     * 요청 헤더: Authorization: Bearer <token> (JWT 필수)
     * 경로 변수: reviewId (리뷰 ID)
     * 요청 바디: HideReviewRequest { "hide": true/false }
     */
//    @PostMapping("/admin/{reviewId}/hide")
//    public ReviewResponse hide(
//            @PathVariable UUID reviewId,
//            @Valid @RequestBody HideReviewRequest req,
//            @RequestHeader("Authorization") String authHeader) {  // ✅ 변경
//
//        // ✅ JWT에서 adminId 추출
//        String token = authHeader.substring(7);  // "Bearer " 제거
//        String adminId = jwtUtil.getUserIdFromToken(token);
//
//        System.out.println("🔒 리뷰 숨김 - adminId: " + adminId + ", reviewId: " + reviewId);
//        return reviewService.hide(reviewId, req.getHide(), adminId);
//    }
}