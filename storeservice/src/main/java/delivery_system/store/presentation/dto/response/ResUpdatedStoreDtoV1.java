package delivery_system.store.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResUpdatedStoreDtoV1 {
    private UUID store_id;
}
