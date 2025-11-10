package delivery_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT 검증 유틸리티
 * UserService에서 발급한 토큰을 검증하고 사용자 정보를 추출합니다.
 */
@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println("✅ ReviewService JWT Secret 로드 완료");
    }

    /**
     * 토큰에서 userId 추출
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    /**
     * 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        try {
            return getExpirationDateFromToken(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("❌ JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 유효성 검증 (userId 포함)
     */
    public Boolean validateToken(String token, String userId) {
        try {
            final String extractedUserId = getUserIdFromToken(token);
            return (extractedUserId.equals(userId) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // ================= 내부 유틸 메서드 =================

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}