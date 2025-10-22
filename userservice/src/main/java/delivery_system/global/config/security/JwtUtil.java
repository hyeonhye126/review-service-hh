package delivery_system.global.config.security;

import delivery_system.user.domain.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;

    @Component
    public class JwtUtil {

        private final Key SECRET_KEY;

        public JwtUtil(@Value("${jwt.secret}") String secret) {
            this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
            System.out.println("✅ Loaded JWT Secret: " + secret);
        }

        // 토큰 유효 시간 (밀리초)
        private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간
        private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

        /**
         * Access Token 생성
         */
        public String generateAccessToken(String userId, UserRole role) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role.name()); // ✅ "role": "CUSTOMER" 형식으로 클레임 추가
            return createToken(claims, userId, ACCESS_TOKEN_EXPIRATION);
        }


        /**
         * Refresh Token 생성
         */
        public String generateRefreshToken(String userId) {
            // Refresh Token에는 별도 클레임이 필요 없습니다.
            return createToken(new HashMap<>(), userId, REFRESH_TOKEN_EXPIRATION);
        }

        /**
         * 토큰 생성 공통 로직
         */
        private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
            return Jwts.builder()
                    .setClaims(claims) // ✅ 커스텀 클레임 설정
                    .setSubject(subject) // subject = userId
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(SECRET_KEY)
                    .compact();
        }

        // 토큰 생성
        public String generateToken(String user_id) {
            return Jwts.builder()
                    .setSubject(user_id) // subject = username
                    .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 유효
                    .signWith(SECRET_KEY)
                    .compact();
        }

        // 토큰에서 user_id 추출
        public String getUserIDFromToken(String token) {
            return getClaimFromToken(token, Claims::getSubject);
        }

        // ✅ [추가] 토큰에서 role 추출
        public String getRoleFromToken(String token) {
            return getClaimFromToken(token, claims -> claims.get("role", String.class));
        }

        // 토큰이 만료됐는지 확인
        public Boolean isTokenExpired(String token) {
            return getExpirationDateFromToken(token).before(new Date());
        }

        // 유효성 검증
        public Boolean validateToken(String token, String username) {
            final String extractedUsername = getUserIDFromToken(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
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
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }

