package delivery_system.cart.domain.Entity; // 💡 package 수정

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RedisHash("cart")
@Getter @Setter
public class Cart implements Serializable {

    @Id
    private String userId;

    private UUID storeId;
    private String storeName;
    private Integer deliveryFee;

    private List<CartItem> items = new ArrayList<>();

    public Cart(String userId, UUID storeId, String storeName, Integer deliveryFee) {
        this.userId = userId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.deliveryFee = deliveryFee;
    }

    public Cart() {}

    public int calculateTotalFee() {
        int itemFee = items.stream()
                .mapToInt(item -> item.getMenuFee() * item.getQuantity() + item.getOptions().stream()
                        .mapToInt(CartItemOpt::getFee)
                        .sum() * item.getQuantity())
                .sum();

        return itemFee + deliveryFee;
    }
}