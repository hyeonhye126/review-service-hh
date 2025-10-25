package delivery_system.admin.presentation.dto.request;

import delivery_system.user.domain.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateRoleDtoV1 {

    private UserRole role;
}
