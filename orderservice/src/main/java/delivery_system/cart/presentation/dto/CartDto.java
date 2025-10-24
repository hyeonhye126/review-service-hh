package delivery_system.cart.presentation.dto;

import delivery_system.cart.domain.Entity.Cart;
import delivery_system.cart.domain.Entity.CartItem;
import delivery_system.cart.domain.Entity.CartItemOpt;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder(toBuilder = true)
public class CartDto {
    private UUID storeId;
    private String storeName;
    private Integer deliveryFee;
    private int totalItemFee;
    private int finalTotalFee;

    // 1. 일반배달/한집배달
    private Boolean isSingleDelivery;
    private long singleDeliveryFee; // 💡 Long -> long으로 변경 (엔티티의 Getter와 일치)

    // 2. 배달 요청사항
    private String ownerRequest;
    private String partnerRequest;

    // 3. 선택된 배달 주소
    private String deliveryAddress;

    // 3. 배달 가능 여부 (조회 후 결정)
    private boolean isAblePay;

    // 5. 결제 상태
    private String payInfo;

    private List<CartItemDto> items;

    public static CartDto from(Cart cart) {
        // 1. 아이템 총 금액 계산
        int totalItemFee = cart.getItems().stream()
                .mapToInt(item -> {
                    int itemBaseFee = item.getMenuFee() + item.getOptions().stream().mapToInt(CartItemOpt::getFee).sum();
                    return itemBaseFee * item.getQuantity();
                })
                .sum();

        // 2. 배달 방식에 따른 최종 배달료 및 총 금액 계산
        long finalDeliveryFee = cart.getDeliveryFee() != null ? cart.getDeliveryFee() : 0;

        // 한집배달(isSingleDelivery가 true)이면 추가 수수료 (1000원) 반영
        if (Boolean.TRUE.equals(cart.getIsSingleDelivery())) {
            // 💡 엔티티의 Getter 호출: 이제 오류가 해결됩니다.
            finalDeliveryFee += cart.getSingleDeliveryFee();
        }

        int finalTotalFee = totalItemFee + (int) finalDeliveryFee;


        return CartDto.builder()
                .storeId(cart.getStoreId())
                .storeName(cart.getStoreName())

                // 새로운 필드 매핑
                .isSingleDelivery(cart.getIsSingleDelivery())
                .singleDeliveryFee(cart.getSingleDeliveryFee()) // 💡 수정된 Getter 호출
                .ownerRequest(cart.getOwnerRequest())
                .partnerRequest(cart.getPartnerRequest())
                .deliveryAddress(cart.getDeliveryAddress())
                .payInfo(cart.getPayInfo())

                .deliveryFee((int) finalDeliveryFee)
                .totalItemFee(totalItemFee)
                .finalTotalFee(finalTotalFee)
                .items(cart.getItems().stream().map(CartItemDto::from).collect(Collectors.toList()))
                .build();
    }

    // 내부 DTO 유지
    @Getter
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

    @Getter
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