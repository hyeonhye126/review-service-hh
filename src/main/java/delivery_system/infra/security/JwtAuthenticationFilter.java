package delivery_system.infra.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * ReviewService용 JWT 인증 필터
 * - Redis 없이 JWT 토큰의 유효성만 검증합니다.
 * - 토큰 발급/관리는 UserService에서만 수행합니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // ✅ RedisTemplate 제거! JwtUtil만 사용
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // ✅ ReviewService: 다음 경로는 JWT 필터 건너뛰기
        return path.equals("/users/signup") ||
                path.equals("/users/login") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html") ||
                (path.startsWith("/api/v1/stores/") &&
                        path.endsWith("/reviews"));  // 공개 리뷰 조회
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // ✅ 토큰에서 userId 추출
                String userId = jwtUtil.getUserIdFromToken(token);

                // ✅ 토큰 유효성 검사 (만료 여부만 확인)
                if (jwtUtil.validateToken(token, userId)) {
                    String role = jwtUtil.getRoleFromToken(token);

                    // ✅ Spring Security가 인식할 수 있는 권한 객체로 변환
                    List<SimpleGrantedAuthority> authorities =
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

                    // ✅ 인증 객체 생성 및 SecurityContext에 저장
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("✅ JWT 인증 성공 - userId: " + userId + ", role: " + role);

                } else {
                    setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            "유효하지 않은 토큰입니다.");
                    return;
                }

            } catch (ExpiredJwtException e) {
                setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "토큰이 만료되었습니다. 다시 로그인해주세요.");
                return;

            } catch (JwtException e) {
                setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                        "잘못된 JWT 형식입니다: " + e.getMessage());
                return;

            } catch (Exception e) {
                setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "JWT 인증 처리 중 오류가 발생했습니다.");
                return;
            }
        }

        // ✅ 토큰이 없는 요청은 그대로 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}