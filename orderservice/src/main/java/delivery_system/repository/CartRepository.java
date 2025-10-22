package delivery_system.repository;

import delivery_system.domain.Cart;
import delivery_system.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(String userId);
}

// CartItemOptRepository는 필요시 추가
