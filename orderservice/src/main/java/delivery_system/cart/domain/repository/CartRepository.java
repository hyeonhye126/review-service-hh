package delivery_system.cart.domain.repository;

import delivery_system.cart.domain.Entity.Cart; // 💡 import 수정
import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findByUserId(String userId);

    Cart save(Cart cart);

    void deleteByUserId(String userId);
}