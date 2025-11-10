package delivery_system.review.application.service;

import delivery_system.common.exception.review.ReviewException;
import delivery_system.review.domain.entity.ReviewEntityV1;
import delivery_system.review.domain.repository.ReviewRepositoryV1;
import delivery_system.review.presentation.dto.request.ReqCreateReviewDtoV1;
import delivery_system.review.presentation.dto.request.ReqUpdateReviewDtoV1;
import delivery_system.review.presentation.dto.response.ResReviewDtoV1;
import delivery_system.review.presentation.dto.response.ResStoreReviewDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepositoryV1 reviewRepository;

    // 가게 아이디로 전체 리뷰 조회
    public ResStoreReviewDtoV1 getReviewByStoreId(UUID storeId) {
        List<ReviewEntityV1> reviews = reviewRepository.findAllByStoreIdAndDeletedAtIsNull(storeId);
        List<ResReviewDtoV1> list = reviews.stream().map(this::convertToResponse).toList();

        // 1. 평점 평균 계산
        OptionalDouble averageRating = list.stream()
                .mapToInt(ResReviewDtoV1::getRating)
                .average();

        // 2. 내용(content)이 있는 리뷰의 개수 계산
        long contentCount = list.stream()
                .filter(review -> review.getContent() != null && !review.getContent().trim().isEmpty())
                .count();

        // 3. 평점 평균 반올림 및 포맷팅
        double rawAverageRating = averageRating.orElse(0.0);
        DecimalFormat df = new DecimalFormat("0.0");
        String formattedAverageRatingString = df.format(rawAverageRating);
        double finalAverageRating = Double.parseDouble(formattedAverageRatingString); // DTO가 double 타입일 경우

        ResStoreReviewDtoV1 response = new ResStoreReviewDtoV1();
        response.setStoreId(storeId);
        response.setStoreRatingAvg(finalAverageRating);
        response.setStoreReviewCount(contentCount);
        response.setReviews(list);

        return response;
    }

    // 주문 아이디로 리뷰 조회
    public ResReviewDtoV1 getReviewByOrderId(String userId, UUID orderId) {
        ReviewEntityV1 review = reviewRepository.findByOrderIdAndCustomerIdAndDeletedAtIsNull(orderId, userId);

        if (review == null) {
            throw new ReviewException("리뷰가 등록되지 않았습니다.");
        }

        return convertToResponse(review);
    }

    // 사용자 아이디로 전체 리뷰 조회
    public List<ResReviewDtoV1> getReviewByCustomerId(String customerId) {
        List<ReviewEntityV1> reviews = reviewRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    //리뷰 생성
    public ResReviewDtoV1 createReview(String customerId, UUID storeId, UUID orderId, ReqCreateReviewDtoV1 createReviewDto) {
        // 기존 작성한 리뷰가 있는지 확인
        ReviewEntityV1 review = reviewRepository.findByOrderIdAndCustomerIdAndDeletedAtIsNull(orderId, customerId);
        if (review != null) {
            throw new ReviewException("이미 등록된 리뷰입니다.");
        }
        ReviewEntityV1 reviewEntity = new ReviewEntityV1();

        reviewEntity.setCustomerId(customerId);
        reviewEntity.setOrderId(orderId);
        reviewEntity.setStoreId(storeId);
        reviewEntity.setRating(createReviewDto.getRating());
        reviewEntity.setContent(createReviewDto.getContent());
        reviewEntity.setCreatedAt(LocalDateTime.now());

        ReviewEntityV1 savedReviewEntity = reviewRepository.save(reviewEntity);
        return  convertToResponse(savedReviewEntity);
    }

    //사용자 아이디에 따른 리뷰 수정
    public ResReviewDtoV1 updateReview(String customerId, UUID reviewId, ReqUpdateReviewDtoV1 updateReviewDto) {
        ReviewEntityV1 reviewEntity = reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId);

        System.out.println("==============================================");
        System.out.println("reviewEntity ====> " + reviewEntity);
        System.out.println("==============================================");

        reviewEntity.setUpdatedAt(LocalDateTime.now());
        reviewEntity.setUpdatedBy(customerId);

        // 별점 수정
        if(updateReviewDto.getRating() != null) {
            reviewEntity.setRating(updateReviewDto.getRating());
        }

        // 내용 수정
        if(updateReviewDto.getContent() != null) {
            reviewEntity.setContent(updateReviewDto.getContent());
        }

        ReviewEntityV1 savedReviewEntity = reviewRepository.save(reviewEntity);
        System.out.println("==============================================");
        System.out.println("savedReviewEntity ====> " + savedReviewEntity);
        System.out.println("==============================================");
        return convertToResponse(savedReviewEntity);
    }

    //사용자 아이디에 따른 리뷰 삭제
    public void deleteReview(String customerId, UUID reviewId) {
        List<ReviewEntityV1> reviews = reviewRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId);
        for (ReviewEntityV1 reviewEntity : reviews) {
            if(reviewEntity.getReviewId().equals(reviewId)) {
                reviewEntity.setDeletedAt(LocalDateTime.now());
                reviewEntity.setDeletedBy(customerId);
                reviewRepository.save(reviewEntity);
                break;
            }
        }
    }

    //사용자 아이디에 따른 전체 리뷰 삭제
    public void deleteAllReviewsByCustomerId(String customerId) {
        List<ReviewEntityV1> reviews = reviewRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId);
        for (ReviewEntityV1 reviewEntity : reviews) {
            reviewEntity.setDeletedAt(LocalDateTime.now());
            reviewEntity.setDeletedBy(customerId);
            reviewRepository.save(reviewEntity);
        }
    }

    //가게 아이디에 따른 전체 리뷰 삭제
    public void deleteAllReviewsByStoreId(UUID storeId) {
        List<ReviewEntityV1> reviews = reviewRepository.findAllByStoreIdAndDeletedAtIsNull(storeId);
        for (ReviewEntityV1 reviewEntity : reviews) {
            reviewEntity.setDeletedAt(LocalDateTime.now());
            reviewEntity.setDeletedBy("store is deleted");
            reviewRepository.save(reviewEntity);
        }
    }

    // 받아 온 리뷰 데이터 반환
    private ResReviewDtoV1 convertToResponse(ReviewEntityV1 reviewEntity) {
        ResReviewDtoV1 response = new ResReviewDtoV1();
        response.setReviewId(reviewEntity.getReviewId());
        response.setOrderId(reviewEntity.getOrderId());
        response.setCustomerId(reviewEntity.getCustomerId());
        response.setStoreId(reviewEntity.getStoreId());
        response.setRating(reviewEntity.getRating());
        response.setContent(reviewEntity.getContent());
        response.setCreatedAt(reviewEntity.getCreatedAt());
        response.setUpdatedAt(reviewEntity.getUpdatedAt());
        response.setUpdatedBy(reviewEntity.getUpdatedBy());
        return response;
    }
}
