package delivery_system.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // 이 인터페이스가 Spring의 Repository(저장소) Bean임을 선언
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    //                                         <관리할 엔티티, 엔티티의 ID 타입>

    /**
     * 1. (최적화) 삭제되지 않은(deletedAt == null) 모든 카테고리 조회
     * - CategoryService의 findAll()에서 사용
     */
    List<Category> findAllByDeletedAtIsNull();

    /**
     * 2. (최적화) 삭제되지 않은(deletedAt == null) 카테고리 1개 조회 (ID 기준)
     * - CategoryService의 findActiveCategoryById()에서 사용
     */
    Optional<Category> findByCategoryIdAndDeletedAtIsNull(UUID catId);

    /**
     * 3. (최적화) 삭제되지 않은(deletedAt == null) 카테고리 1개 조회 (이름 기준)
     * - CategoryService의 validateDuplicateCategoryName()에서 사용
     */
    Optional<Category> findByCategoryNameAndDeletedAtIsNull(String catName);
}