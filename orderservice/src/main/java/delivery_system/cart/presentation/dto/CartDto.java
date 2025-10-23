package delivery_system.cart.presentation.dto;

import delivery_system.cart.domain.Entity.Cart; // 💡 import 수정
import delivery_system.cart.domain.Entity.CartItem; // 💡 import 수정
import delivery_system.cart.domain.Entity.CartItemOpt; // 💡 import 수정
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class CartDto {
    private UUID storeId;
    private String storeName;
    private Integer deliveryFee;
    private int totalItemFee;
    private int finalTotalFee;
    private List<CartItemDto> items;

    public static CartDto from(Cart cart) {
        int totalItemFee = cart.getItems().stream()
                .mapToInt(item -> {
                    int itemBaseFee = item.getMenuFee() + item.getOptions().stream().mapToInt(CartItemOpt::getFee).sum();
                    return itemBaseFee * item.getQuantity();
                })
                .sum();

        return CartDto.builder()
                .storeId(cart.getStoreId())
                .storeName(cart.getStoreName())
                .deliveryFee(cart.getDeliveryFee())
                .totalItemFee(totalItemFee)
                .finalTotalFee(totalItemFee + cart.getDeliveryFee())
                .items(cart.getItems().stream().map(CartItemDto::from).collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class CartItemDto {
        private UUID cartItemId;
        private UUID menuId;
        private String menuName;
        private int menuFee;
        private int quantity;
        private int itemTotalPrice;
        private List<CartItemOptDto> options;

        public static CartItemDto from(CartItem item) {
            int optionsTotalFee = item.getOptions().stream().mapToInt(CartItemOpt::getFee).sum();
            int itemTotalPrice = (item.getMenuFee() + optionsTotalFee) * item.getQuantity();

            return CartItemDto.builder()
                    .cartItemId(item.getCartItemId())
                    .menuId(item.getMenuId())
                    .menuName(item.getMenuName())
                    .menuFee(item.getMenuFee())
                    .quantity(item.getQuantity())
                    .itemTotalPrice(itemTotalPrice)
                    .options(item.getOptions().stream().map(CartItemOptDto::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @Builder
    public static class CartItemOptDto {
        private UUID menuOptValueId;
        private String menuOptValueName;
        private int fee;

        public static CartItemOptDto from(CartItemOpt opt) {
            return CartItemOptDto.builder()
                    .menuOptValueId(opt.getMenuOptValueId())
                    .menuOptValueName(opt.getMenuOptValueName())
                    .fee(opt.getFee())
                    .build();
        }
    }
}