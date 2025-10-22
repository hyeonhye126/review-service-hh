package delivery_system.dto;

import delivery_system.domain.Cart;
import delivery_system.domain.CartItem;
import delivery_system.domain.CartItemOpt;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class CartDto {
    private UUID cartId;
    private UUID storeId;
    private Integer totalFee;
    private Integer deliveryFee;
    private Integer itemFee;
    private List<CartItemDto> items;

    public static CartDto from(Cart cart) {
        return CartDto.builder()
                .cartId(cart.getCartId())
                .storeId(cart.getStoreId())
                .totalFee(cart.getTotalFee())
                .deliveryFee(cart.getDeliveryFee())
                .itemFee(cart.getItemFee())
                .items(cart.getItems().stream().map(CartItemDto::from).collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class CartItemDto {
        private UUID cartItemId;
        private UUID menuId;
        private String menuName;
        private Integer cartItemFee;
        private Integer cartItemQuantity;
        private List<CartItemOptDto> options;

        public static CartItemDto from(CartItem item) {
            return CartItemDto.builder()
                    .cartItemId(item.getCartItemId())
                    .menuId(item.getMenuId())
                    .menuName(item.getMenuName())
                    .cartItemFee(item.getCartItemFee())
                    .cartItemQuantity(item.getCartItemQuantity())
                    .options(item.getOptions().stream().map(CartItemOptDto::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @Builder
    public static class CartItemOptDto {
        private UUID cartItemOptId;
        private UUID menuOptId;
        private String menuOptName;
        private UUID menuOptValueId;
        private String menuOptValueName;
        private Integer menuOptValueFee;

        public static CartItemOptDto from(CartItemOpt opt) {
            return CartItemOptDto.builder()
                    .cartItemOptId(opt.getCartItemOptId())
                    .menuOptId(opt.getMenuOptId())
                    .menuOptName(opt.getMenuOptName())
                    .menuOptValueId(opt.getMenuOptValueId())
                    .menuOptValueName(opt.getMenuOptValueName())
                    .menuOptValueFee(opt.getMenuOptValueFee())
                    .build();
        }
    }
}