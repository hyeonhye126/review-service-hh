package delivery_system.repository;

import delivery_system.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    // userId의 장바구니에 해당 cartItemId가 있는지 확인하기 위한 쿼리
    Optional<CartItem> findByCartItemIdAndCart_UserId(UUID cartItemId, String userId);
}
