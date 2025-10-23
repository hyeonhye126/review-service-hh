package delivery_system.store.domain.repository;

import delivery_system.store.domain.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    List<StoreEntity> findAllByDeletedAtIsNull();
    StoreEntity findStoreByStoreId(UUID storeId);
}
