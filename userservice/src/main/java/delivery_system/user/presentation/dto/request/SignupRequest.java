package delivery_system.user.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "닉네임은 영문과 숫자 조합으로 5~20자 이내여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "비밀번호는 8~20자이며, 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    private String address;
    private String role;
}

