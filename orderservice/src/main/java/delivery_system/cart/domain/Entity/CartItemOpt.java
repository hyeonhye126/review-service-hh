package delivery_system.cart.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_cart_item_opt")
@Getter @Setter
public class CartItemOpt {
    @Id
    @Column(name = "cart_item_opt_id")
    private UUID cartItemOptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem; // FK

    @Column(name = "menu_opt_id", nullable = false)
    private UUID menuOptId;

    @Column(name = "menu_opt_name", length = 200)
    private String menuOptName;

    @Column(name = "menu_opt_value_id", nullable = false)
    private UUID menuOptValueId; // FK

    @Column(name = "menu_opt_value_name", length = 200)
    private String menuOptValueName;

    @Column(name = "menu_opt_value_fee", nullable = false)
    private Integer menuOptValueFee = 0; // 옵션 가격
}