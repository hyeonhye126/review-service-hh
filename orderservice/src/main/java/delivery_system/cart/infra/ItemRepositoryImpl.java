package delivery_system.cart.infra;

import delivery_system.cart.domain.Entity.Item; // ⭐️ Item으로 변경
import delivery_system.cart.domain.repository.ItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// JPA Repository (실제 DB 접근 인터페이스)
// ⭐️ Item으로 변경
interface JpaItemRepository extends JpaRepository<Item, UUID> {

    // Item(p_menu) -> ItemOpt(p_menu_opt) -> ItemOptionValue(p_menu_opt_value)를 조인
    // JPA는 Item 엔티티의 필드 이름을 사용합니다.
    @Query("SELECT i FROM Item i " +
            // i.options는 Item.java의 필드명 (Set<ItemOpt>)
            "LEFT JOIN FETCH i.options o " +
            // o.values는 ItemOpt.java의 필드명 (Set<ItemOptionValue>)
            "LEFT JOIN FETCH o.values v " +
            "WHERE i.itemId = :itemId")
    Optional<Item> findItemWithDetailsById(@Param("itemId") UUID itemId);
}

// Domain 인터페이스 구현체
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final JpaItemRepository jpaItemRepository;

    public ItemRepositoryImpl(JpaItemRepository jpaItemRepository) {
        this.jpaItemRepository = jpaItemRepository;
    }

    @Override
    public Optional<Item> findItemWithDetailsById(UUID itemId) {
        return jpaItemRepository.findItemWithDetailsById(itemId);
    }
}