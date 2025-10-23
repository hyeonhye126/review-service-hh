package delivery_system.cart.presentation.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Data
public class CartAddItemRequest {

    @NotNull
    private UUID menuId;

    @Min(1)
    private int quantity;

    private List<OptionDto> options;

    @Data
    public static class OptionDto {
        @NotNull
        private UUID menuOptValueId;
    }
}