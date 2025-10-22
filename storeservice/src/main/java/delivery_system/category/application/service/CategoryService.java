package delivery_system.category.application.service;

import delivery_system.category.domain.entity.CategoryEntity;
import delivery_system.category.domain.repository.CategoryRepository;
// ✅ [수정] 잘못된 import 삭제 (import delivery_system.category.presentation.dto.request.CategoryUpdate;)
import delivery_system.global.infra.dto.CategoryCreateRequest;
import delivery_system.global.infra.dto.CategoryResponse;
import delivery_system.global.infra.dto.CategoryUpdateRequest; // ✅ [수정] 올바른 DTO 임포트
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    /**
     * 1. 모든 카테고리 조회 (삭제되지 않은 것만)
     */
    public List<CategoryResponse> findAll() {
        log.info("모든 카테고리 목록 조회 시도 (삭제된 것 제외)");
        return categoryRepository.findAllByDeletedAtIsNull().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 2. 카테고리 생성 (MANAGER 전용)
     */
    @Transactional // 쓰기 작업
    public CategoryResponse createCategory(CategoryCreateRequest createRequest, String managerId) {

        // ✅ [디버깅] 1. 메서드 진입 및 파라미터 확인
        log.info("🚀 [1/4] createCategory 호출됨. managerId: {}, 요청된 이름: {}", managerId, createRequest.getCategory_name());
        // ✅ [수정] getCategoryName() -> getCatName()으로 통일
        validateDuplicateCategoryName(createRequest.getCategory_name(), null);
// ✅    [디버깅] 2. 중복 검사 통과
        log.info("✅ [2/4] 중복 이름 검사 통과: {}", createRequest.getCategory_name());
        // 2. 엔티티 생성 및 값 설정
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoryName(createRequest.getCategory_name());
        categoryEntity.setCreatedBy(managerId); // String 타입으로 가정

        // 3. 저장
        // ✅ [디버깅] 3. DB 저장 시도
        log.info("... [3/4] DB에 Category 엔티티 저장 시도...");
        CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity);

        // 4. DTO로 변환하여 반환
        // ✅ [디버깅] 4. 저장 성공
        log.info("🎉 [4/4] DB 저장 성공! 생성된 ID: {}", savedCategoryEntity.getCategoryId());
        return convertToResponse(savedCategoryEntity);
    }

    /**
     * 3. 카테고리 수정 (MANAGER 전용)
     */
    @Transactional
    // ✅ [수정] 파라미터 타입을 CategoryUpdate -> CategoryUpdateRequest DTO로 변경
    public CategoryResponse updateCategory(UUID categoryId, @Valid CategoryUpdateRequest updateRequest, String managerId) {
        log.info("🚀 updateCategory 호출됨. categoryId: {}", categoryId);
        // 1. 엔티티 조회 (최적화된 메서드 사용)
        CategoryEntity categoryEntity = findActiveCategoryById(categoryId);

        // 2. 중복 이름 검사 (최적화된 메서드 사용)
        validateDuplicateCategoryName(updateRequest.getCategoryName(), categoryId);

        // 3. 값 수정
        categoryEntity.setCategoryName(updateRequest.getCategoryName());
        categoryEntity.setUpdatedBy(managerId);
        // @UpdateTimestamp가 updatedAt은 자동 설정

        CategoryEntity updatedCategoryEntity = categoryRepository.save(categoryEntity);
        log.info("✅ 카테고리 수정 완료. ID: {}", categoryId);
        return convertToResponse(updatedCategoryEntity);
    }

    /**
     * 4. 카테고리 삭제 (소프트 삭제) (MANAGER 전용)
     */
    @Transactional
    public void deleteCategory(UUID categoryId, String managerId) {
        // 1. 엔티티 조회 (최적화된 메서드 사용)
        CategoryEntity categoryEntity = findActiveCategoryById(categoryId);

        // 2. 소프트 삭제 처리
        categoryEntity.setDeletedAt(LocalDateTime.now());
        categoryEntity.setDeletedBy(managerId);

        // 3. 저장
        categoryRepository.save(categoryEntity);
    }

    // --- Helper Methods (내부에서만 사용하는 보조 메서드) ---

    /**
     * Category 엔티티를 CategoryResponse로 변환
     */
    private CategoryResponse convertToResponse(CategoryEntity categoryEntity) {
        CategoryResponse response = new CategoryResponse();
        // ✅ [수정] getCategoryId() -> getCatId()로 통일
        response.setCategoryId(categoryEntity.getCategoryId());
        response.setCategoryName(categoryEntity.getCategoryName());
        response.setCreatedAt(categoryEntity.getCreatedAt());
        return response;
    }

    /**
     * (공통) 삭제되지 않은 활성 카테고리 조회 (최적화 완료)
     */
    private CategoryEntity findActiveCategoryById(UUID categoryId) {
        return categoryRepository.findByCategoryIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다. ID: " + categoryId));
    }

    /**
     * (공통) 카테고리 이름 중복 검사 (최적화 완료)
     */
    private void validateDuplicateCategoryName(String categoryName, UUID currentId) {
        Optional<CategoryEntity> existingCategory = categoryRepository.findByCategoryNameAndDeletedAtIsNull(categoryName);

        if (existingCategory.isPresent()) {
            if (currentId == null) { // 생성 시
                throw new DataIntegrityViolationException("이미 존재하는 카테고리 이름입니다: " + categoryName);
            }
            // 수정 시, 자기 자신이 아닌데 이름이 겹칠 때
            // ✅ [수정] getCategoryId() -> getCatId()로 통일
            if (!existingCategory.get().getCategoryId().equals(currentId)){
                throw new DataIntegrityViolationException("이미 존재하는 카테고리 이름입니다: " + categoryName);
            }
        }
    }
}