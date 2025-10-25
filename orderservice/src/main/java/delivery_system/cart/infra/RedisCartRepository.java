package delivery_system.cart.infra;

import delivery_system.cart.domain.Entity.Cart; // 💡 import 수정
import delivery_system.cart.domain.repository.CartRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Repository
public class RedisCartRepository implements CartRepository {

    private static final String CART_HASH_KEY = "Cart_Session";
    private final RedisTemplate<String, Cart> redisTemplate;
    private HashOperations<String, String, Cart> hashOperations;

    public RedisCartRepository(RedisTemplate<String, Cart> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return Optional.ofNullable(hashOperations.get(CART_HASH_KEY, userId));
    }

    @Override
    public Cart save(Cart cart) {
        hashOperations.put(CART_HASH_KEY, cart.getUserId(), cart);
        return cart;
    }

    @Override
    public void deleteByUserId(String userId) {
        hashOperations.delete(CART_HASH_KEY, userId);
    }
}