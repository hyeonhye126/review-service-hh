package delivery_system.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

// POST /api/v1/cart 요청 DTO
@Data
public class CartAddItemRequest {
    @NotNull private UUID storeId;
    @NotNull private UUID menuId;
    private int quantity = 1; // 수량
    private List<OptionDto> options;

    @Data
    public static class OptionDto {
        @NotNull private UUID menuOptId;
        @NotNull private UUID menuOptValueId;
    }
}

// PATCH /api/v1/cart/items/{cart_item_id} 요청 DTO
@Data
public class CartItemQuantityRequest {
    @Min(1) private int cartItemQuantity;
}

// POST /api/v1/cart/items/{cart_item_id}/opts 요청 DTO
@Data
public class CartItemOptAddRequest {
    @NotNull private List<CartAddItemRequest.OptionDto> options;
}

// PATCH /api/v1/cart/items/{cart_item_id}/opts/{cart_item_opt_id} 요청 DTO
@Data
public class CartItemOptUpdateRequest {
    @NotNull private UUID menuOptValueId;
}