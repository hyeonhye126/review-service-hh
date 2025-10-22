// src/main/java/delivery_system/cart/domain/repository/ItemRepository.java
package delivery_system.cart.domain.repository;

import delivery_system.cart.domain.Entity.Item; // ⭐️ Item으로 변경
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    // ID로 Item 정보와 관련된 옵션 정보까지 한 번에 조회합니다.
    Optional<Item> findItemWithDetailsById(UUID itemId);
}