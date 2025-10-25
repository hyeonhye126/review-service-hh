package delivery_system.user.presentation.dto.response;


import lombok.Getter;

// (Lombok을 사용한다면)
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
//
// @Getter
// @RequiredArgsConstructor
@Getter
public class TokenResponse {

    // Getter
    private final String accessToken;
    private final String refreshToken;

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
