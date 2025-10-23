package delivery_system.cart.domain.Entity; // 💡 package 수정

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class CartItemOpt implements Serializable {

    private UUID cartItemOptId;
    private UUID menuOptId;
    private String menuOptName;
    private UUID menuOptValueId;
    private String menuOptValueName;
    private int fee;
}