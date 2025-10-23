package delivery_system.store.application.service;

import delivery_system.store.domain.entity.StoreEntity;
import delivery_system.store.domain.repository.StoreRepository;
import delivery_system.store.presentation.dto.request.ReqCreateStoreDtoV1;
import delivery_system.store.presentation.dto.response.ResCreateStoreDtoV1;
import delivery_system.store.presentation.dto.response.ResGetStoreByIdDtoV1;
import delivery_system.store.presentation.dto.response.ResGetStoresDtoV1;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class StoreService {

    private final StoreRepository storeRepository;
    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    public List<ResGetStoresDtoV1> findAll() {
        log.debug("스토어 정보 조회");
        return storeRepository.findAllByDeletedAtIsNull().stream()
                .map(this::convertToResponseList)
                .collect(Collectors.toList());
    }

    public ResGetStoreByIdDtoV1 findByStoreId(UUID storeId) {
        log.debug("storeId ====> {}",  storeId);
        StoreEntity storeEntity = storeRepository.findStoreByStoreId(storeId);
        return convertToStore(storeEntity);
    }

    public ResCreateStoreDtoV1 createStore(ReqCreateStoreDtoV1 reqCreateStoreDtoV1) {
        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setStoreName(reqCreateStoreDtoV1.getStoreName());
        storeEntity.setStoreAddress(reqCreateStoreDtoV1.getStoreAddress());
        storeEntity.setDescription(reqCreateStoreDtoV1.getDescription());
        storeEntity.setOwnerId(reqCreateStoreDtoV1.getOwnerId());
        storeEntity.setStoreRatingAvg(reqCreateStoreDtoV1.getStoreRatingAvg());
        storeEntity.setStoreReviewCount(reqCreateStoreDtoV1.getStoreReviewCount());

        StoreEntity savedStoreEntity = storeRepository.save(storeEntity);

        return convertToCreatedStore(savedStoreEntity);
    }

    private ResCreateStoreDtoV1 convertToCreatedStore(StoreEntity storeEntity) {
        ResCreateStoreDtoV1 response = new ResCreateStoreDtoV1();
        log.info(response.toString());
        response.setStoreId(storeEntity.getStoreId());
        response.setOwnerId(storeEntity.getOwnerId());
        response.setStoreName(storeEntity.getStoreName());
        response.setDescription(storeEntity.getDescription());
        response.setStoreAddress(storeEntity.getStoreAddress());
        response.setStoreRatingAvg(storeEntity.getStoreRatingAvg());
        response.setStoreReviewCount(storeEntity.getStoreReviewCount());

        return response;
    }

    private ResGetStoreByIdDtoV1 convertToStore(StoreEntity storeEntity) {
        ResGetStoreByIdDtoV1 response = new ResGetStoreByIdDtoV1();
        log.info(response.toString());
        // ✅ [수정] getCategoryId() -> getCatId()로 통일
        response.setStoreId(storeEntity.getStoreId());
        response.setOwnerId(storeEntity.getOwnerId());
        response.setStoreName(storeEntity.getStoreName());
        response.setDescription(storeEntity.getDescription());
        response.setDeliveryFee(storeEntity.getDeliveryFee());
        response.setStoreAddress(storeEntity.getStoreAddress());
        response.setStoreGeom(storeEntity.getStoreGeom());
        response.setStoreRatingAvg(storeEntity.getStoreRatingAvg());
        response.setStoreReviewCount(storeEntity.getStoreReviewCount());
        return response;
    }

    private ResGetStoresDtoV1 convertToResponseList(StoreEntity storeEntity) {
        ResGetStoresDtoV1 response = new ResGetStoresDtoV1();
        log.info(response.toString());
        // ✅ [수정] getCategoryId() -> getCatId()로 통일
        response.setStoreId(storeEntity.getStoreId());
        response.setOwnerId(storeEntity.getOwnerId());
        response.setStoreName(storeEntity.getStoreName());
        response.setDescription(storeEntity.getDescription());
        response.setDeliveryFee(storeEntity.getDeliveryFee());
        response.setStoreAddress(storeEntity.getStoreAddress());
        response.setStoreGeom(storeEntity.getStoreGeom().toString());
        response.setStoreRatingAvg(storeEntity.getStoreRatingAvg());
        response.setStoreReviewCount(storeEntity.getStoreReviewCount());
        return response;
    }
}
