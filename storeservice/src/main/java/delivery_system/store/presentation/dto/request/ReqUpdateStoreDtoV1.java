package delivery_system.store.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateStoreDtoV1 {

    @JsonProperty("store_name")
    private String storeName; // 가게명

    @JsonProperty("description")
    private String description; // 가게 소개

    @JsonProperty("store_address")
    private String storeAddress; // 주소 원문
}
