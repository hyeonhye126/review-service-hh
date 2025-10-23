package delivery_system.cart.presentation.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

// PostgreSQL JSONB 쿼리 결과를 매핑하는 DTO
@Data
public class MenuDetailsDto {
    private UUID menuId;
    private UUID storeId;
    private String storeName;
    private int deliveryFee;
    private String menuName;
    private int menuFee;
    private List<OptionGroupDto> options;

    @Data
    public static class OptionGroupDto {
        private UUID menuOptId;
        private String menuOptName;
        private List<OptionValueDto> values;
    }

    @Data
    public static class OptionValueDto {
        private UUID menuOptValueId;
        private String valueName;
        private int fee;
    }
}