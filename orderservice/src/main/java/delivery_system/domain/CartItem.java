package delivery_system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_cart_item")
@Getter @Setter
public class CartItem {
    @Id
    @Column(name = "cart_item_id")
    private UUID cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; // FK

    @Column(name = "menu_id", nullable = false)
    private UUID menuId; // FK

    @Column(name = "menu_name", nullable = false, length = 200)
    private String menuName;

    @Column(name = "cart_item_fee", nullable = false)
    private Integer cartItemFee; // 메뉴 기본 가격

    @Column(name = "cart_item_quantity", nullable = false)
    private Integer cartItemQuantity = 1;

    // 장바구니 항목 옵션: 1:N 관계 설정
    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemOpt> options = new ArrayList<>();
}