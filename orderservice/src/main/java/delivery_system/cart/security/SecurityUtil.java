package delivery_system.cart.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Spring Security Context에서 현재 인증된 사용자의 정보를 추출하는 유틸리티 클래스입니다.
 * JwtAuthenticationFilter가 인증 정보를 SecurityContext에 성공적으로 저장했을 때만 유효합니다.
 */
@Component
public class SecurityUtil {

    /**
     * 현재 로그인된 사용자 ID (username)를 반환합니다.
     * @return 현재 사용자의 ID (String)
     * @throws RuntimeException 인증 정보가 Security Context에 없을 경우
     */
    public static String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증 객체가 없거나 인증되지 않은 경우 (e.g., 익명 사용자, 토큰 없음 등)
            throw new RuntimeException("인증 정보가 Security Context에 없습니다. 접근이 거부됩니다.");
        }

        Object principal = authentication.getPrincipal();
        String userId = null;

        if (principal instanceof UserDetails userDetails) {
            // UserDetails 객체 (일반적인 Spring Security User 객체)
            userId = userDetails.getUsername();
        } else if (principal instanceof String principalString) {
            // UsernamePasswordAuthenticationToken을 사용할 때, 첫 번째 인자(Principal)로 전달된 문자열
            // (JwtAuthenticationFilter에서 username을 principal로 설정했으므로 이 경우가 발생합니다.)
            userId = principalString;
        }

        if (userId == null || "anonymousUser".equals(userId)) {
            throw new RuntimeException("인증되었으나 사용자 ID를 찾을 수 없거나 익명 사용자입니다.");
        }

        return userId;
    }
}