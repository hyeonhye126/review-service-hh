// delivery_system.order.client.MenuFeignClient.java (MenuInfo 구조도 가정)
package delivery_system.application;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

// 실제로는 FeignClient 어노테이션을 사용하거나 WebClient를 사용해야 합니다.
// 여기서는 Service 로직 완성을 위해 임시로 Component를 사용합니다.
@Component
public class MenuFeignClient {
    // 외부 메뉴 서비스로부터 받아오는 메뉴 및 옵션 정보 구조
    @Data
    public static class MenuInfo {
        private UUID menuId;
        private String menuName;
        private int menuPrice;
        private List<OptionInfo> options;
    }

    @Data
    public static class OptionInfo {
        private UUID menuOptId;
        private String menuOptName;
        private UUID menuOptValueId;
        private String menuOptValueName;
        private int menuOptValueFee;
    }

    // 실제로는 외부 API 호출 로직이 들어갑니다.
    // 여기서는 항상 더미 데이터를 반환한다고 가정합니다.
    public MenuInfo getMenuPriceAndInfo(UUID menuId, List<UUID> optionValueIds) {
        MenuInfo info = new MenuInfo();
        info.setMenuId(menuId);
        info.setMenuName("짬뽕");
        info.setMenuPrice(10000);

        List<OptionInfo> opts = List.of(
                createOpt(UUID.fromString("uuid-맵기"), "맵기", UUID.fromString("uuid-4단계"), "4단계", 500),
                createOpt(UUID.fromString("uuid-사이즈"), "사이즈", UUID.fromString("uuid-곱빼기"), "곱빼기", 1000)
        );

        // 요청된 optionValueIds에 해당하는 옵션만 필터링하는 로직 필요
        info.setOptions(opts);
        return info;
    }

    private OptionInfo createOpt(UUID optId, String optName, UUID valId, String valName, int fee) {
        OptionInfo opt = new OptionInfo();
        opt.setMenuOptId(optId);
        opt.setMenuOptName(optName);
        opt.setMenuOptValueId(valId);
        opt.setMenuOptValueName(valName);
        opt.setMenuOptValueFee(fee);
        return opt;
    }
}