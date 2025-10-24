package delivery_system.cart.domain.Entity; // 💡 package 수정

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CartItem implements Serializable {

    private UUID cartItemId;
    private UUID menuId;
    private String menuName;
    private int menuFee;
    private int quantity;

    private List<CartItemOpt> options = new ArrayList<>();
}