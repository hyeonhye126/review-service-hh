package delivery_system.store.application.service;

import delivery_system.store.domain.entity.StoreEntity;
import delivery_system.store.domain.repository.StoreRepository;
import delivery_system.store.presentation.dto.response.ResGetStoreByIdDtoV1;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class StoreService {

    private final StoreRepository storeRepository;
    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    public List<ResGetStoreByIdDtoV1> findAll() {
        log.info("스토어 정보 조회");
        return storeRepository.findAllByDeletedAtIsNull().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ResGetStoreByIdDtoV1 convertToResponse(StoreEntity storeEntity) {
        ResGetStoreByIdDtoV1 response = new ResGetStoreByIdDtoV1();
        // ✅ [수정] getCategoryId() -> getCatId()로 통일
        response.setStoreId(storeEntity.getStore_id());
        response.setOwnerId(storeEntity.getOwner_id());
        response.setStoreName(storeEntity.getStoreName());
        response.setDescription(storeEntity.getDescription());
        response.setDeliveryFee(storeEntity.getDeliveryFee());
        response.setStoreAddress(storeEntity.getStoreAddress());
        response.setStoreGeom(storeEntity.getStoreGeom());
        response.setStoreRatingAvg(storeEntity.getStoreRatingAvg());
        response.setStoreReviewCount(storeEntity.getStoreReviewCount());
        return response;
    }
}
