package delivery_system.cart.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_cart")
@Getter @Setter
public class Cart {
    @Id
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "user_id", unique = true, nullable = false, length = 50)
    private String userId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "coupon_fee")
    private Integer couponFee = 0; // NOT NULL 제약조건이 없으므로 null 가능하지만, 0으로 초기화

    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    @Column(name = "item_fee", nullable = false)
    private Integer itemFee = 0; // 메뉴 합계 금액 (수량 * 단가)

    @Column(name = "total_fee", nullable = false)
    private Integer totalFee = 0; // item_fee - coupon_fee + delivery_fee

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // 업데이트 시각 자동 업데이트
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 장바구니 항목: 1:N 관계 설정 (Cart 삭제 시 Item도 같이 삭제)
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}