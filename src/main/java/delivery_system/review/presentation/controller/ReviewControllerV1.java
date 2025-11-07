package delivery_system.review.presentation.controller;

import delivery_system.global.presentation.response.BaseResponse;
import delivery_system.review.application.service.ReviewServiceV1;
import delivery_system.review.presentation.dto.request.ReqCreateReviewDtoV1;
import delivery_system.review.presentation.dto.request.ReqUpdateReviewDtoV1;
import delivery_system.review.presentation.dto.response.ResReviewDtoV1;
import delivery_system.review.presentation.dto.response.ResStoreReviewDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewControllerV1 {

    private final ReviewServiceV1 reviewService;

    // 가게 아이디로 리뷰 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<BaseResponse<ResStoreReviewDtoV1>> reviewsByStoreId(
            @PathVariable UUID storeId) {

        ResStoreReviewDtoV1 reviews = reviewService.getReviewByStoreId(storeId);
        BaseResponse<ResStoreReviewDtoV1> response = BaseResponse.onSuccess("가게 리뷰 조회에 성공하였습니다.", reviews);

        return ResponseEntity.ok(response);
    }

    // 주문건 리뷰 조회
    @GetMapping("/{userId}/order/{orderId}")
    public ResponseEntity<BaseResponse<ResReviewDtoV1>> reviewsByOrderId(
            @PathVariable String userId, @PathVariable UUID orderId) {

        ResReviewDtoV1 reviews = reviewService.getReviewByOrderId(userId, orderId);
        BaseResponse<ResReviewDtoV1> response = BaseResponse.onSuccess("주문 리뷰 조회에 성공하였습니다.", reviews);

        return ResponseEntity.ok(response);
    }

    // 사용자 아이디로 리뷰 조회
    @GetMapping("/user/{customerId}")
    public ResponseEntity<BaseResponse<List<ResReviewDtoV1>>> reviewsByCustomerId(
            @PathVariable String customerId) {

        List<ResReviewDtoV1> reviews = reviewService.getReviewByCustomerId(customerId);
        BaseResponse<List<ResReviewDtoV1>> response = BaseResponse.onSuccess("사용자 리뷰 조회에 성공하였습니다.", reviews);

        return ResponseEntity.ok(response);
    }

    //리뷰 생성
    @PostMapping("/{userId}/{storeId}/{orderId}/create")
    public ResponseEntity<BaseResponse<ResReviewDtoV1>> createReview(
            @PathVariable String userId,
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @RequestBody ReqCreateReviewDtoV1 createReviewDto
    ) {
        ResReviewDtoV1 resReviewDtoV1 = reviewService.createReview(userId, storeId, orderId, createReviewDto);
        BaseResponse<ResReviewDtoV1> response = BaseResponse.onSuccess("리뷰 등록에 성공하였습니다.", resReviewDtoV1);
        return ResponseEntity.ok(response);
    }

    //사용자 아이디에 따른 리뷰 수정
    @PutMapping("/{userId}/{reviewId}")
    public ResponseEntity<BaseResponse<ResReviewDtoV1>> updateReview(
            @PathVariable String userId,
            @PathVariable UUID reviewId,
            @RequestBody ReqUpdateReviewDtoV1 updateReviewDto
    ) {
        ResReviewDtoV1 resReviewDtoV1 = reviewService.updateReview(userId, reviewId, updateReviewDto);
        BaseResponse<ResReviewDtoV1> response = BaseResponse.onSuccess("리뷰 수정에 성공하였습니다.", resReviewDtoV1);
        return ResponseEntity.ok(response);
    }

    //사용자 아이디에 따른 리뷰 삭제
    @DeleteMapping("/{userId}/delete/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReviewByCustomerId(
            @PathVariable String userId,
            @PathVariable UUID reviewId
    ) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    //사용자 아이디에 따른 전체 리뷰 삭제
    @DeleteMapping("/{userId}/deleteAll")
    public ResponseEntity<BaseResponse<Void>> deleteAllReviewByCustomerId(
            @PathVariable String userId
    ) {
        reviewService.deleteAllReviewsByCustomerId(userId);
        return ResponseEntity.noContent().build();
    }
}
