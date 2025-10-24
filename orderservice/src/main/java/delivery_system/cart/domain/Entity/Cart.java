package delivery_system.cart.domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RedisHash("cart")
@Getter @Setter
@NoArgsConstructor
public class Cart implements Serializable {

    @Id
    private String userId;

    private UUID storeId;
    private String storeName;
    private Integer deliveryFee;

    // --- [1. 배달 방식 및 금액] ---
    private Boolean isSingleDelivery = false;

    private static final long SINGLE_DELIVERY_FEE = 1000L;

    // 💡 오류 해결: DTO가 상수에 접근할 수 있도록 Getter를 추가합니다.
    @JsonIgnore
    public long getSingleDeliveryFee() {
        return SINGLE_DELIVERY_FEE;
    }

    // --- [2. 배달 요청사항] ---
    private String ownerRequest;
    private String partnerRequest;

    // --- [3. 선택된 배달 주소] ---
    private String deliveryAddress;

    // --- [5. 결제 상태] ---
    private String payInfo = "결제대기";


    private List<CartItem> items = new ArrayList<>();

    // --- 생성자들 (유지) ---
    public Cart(String userId, UUID storeId, String storeName, Integer deliveryFee,
                Boolean isSingleDelivery, String ownerRequest, String partnerRequest,
                String deliveryAddress, String payInfo) {

        this.userId = userId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.deliveryFee = deliveryFee;
        this.isSingleDelivery = isSingleDelivery != null ? isSingleDelivery : false;
        this.ownerRequest = ownerRequest;
        this.partnerRequest = partnerRequest;
        this.deliveryAddress = deliveryAddress;
        this.payInfo = payInfo;
    }

    public Cart(String userId, UUID storeId, String storeName, Integer deliveryFee) {
        this.userId = userId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.deliveryFee = deliveryFee;
    }


    /**
     * 메뉴 총 금액 + 기본 배달비 + 한집배달 수수료를 포함하여 최종 금액을 계산합니다.
     */
    public int calculateTotalFee() {
        int itemFee = items.stream()
                .mapToInt(item -> item.getMenuFee() * item.getQuantity() + item.getOptions().stream()
                        .mapToInt(CartItemOpt::getFee)
                        .sum() * item.getQuantity())
                .sum();

        int finalDeliveryFee = this.deliveryFee != null ? this.deliveryFee : 0;

        if (Boolean.TRUE.equals(this.isSingleDelivery)) {
            finalDeliveryFee += SINGLE_DELIVERY_FEE;
        }

        return itemFee + finalDeliveryFee;
    }
}