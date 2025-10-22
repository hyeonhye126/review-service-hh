package delivery_system.global.infra.config.security;

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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component; // ⬅️ [필수] 이 import 추가

@Component // ⬅️ [필수] 이 어노테이션을 추가해서 스프링 빈으로 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> authRedisTemplate;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   RedisTemplate<String, String> authRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.authRedisTemplate = authRedisTemplate;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // signup, login, swagger 경로는 JWT 필터 건너뛰기
        return path.equals("/users/signup") ||
                path.equals("/users/login") ||
                path.startsWith("/users/swagger-ui") ||
                path.startsWith("/users/v3/api-docs") ||
                path.equals("/users/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // ✅ 블랙리스트 확인
                if (authRedisTemplate.hasKey("blacklist:" + token)) {
                    setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
                    return;
                }
                // ✅ 토큰에서 username 추출
                String username = jwtUtil.getUserIDFromToken(token);

                // ✅ 토큰 유효성 검사 + Redis 저장된 토큰 일치 여부 확인
                if (jwtUtil.validateToken(token, username)) {
                    String savedToken = authRedisTemplate.opsForValue().get("access:" + username);
                    if (savedToken != null && savedToken.equals(token)) {
                        String role = jwtUtil.getRoleFromToken(token);

                        // 2. Spring Security가 인식할 수 있는 권한 객체로 변환 ( "ROLE_CUSTOMER" )
                        // ❗ "ROLE_" 접두사는 Spring Security의 기본 규칙입니다.
                        List<SimpleGrantedAuthority> authorities =
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

                        // 3. (사용자ID, 자격증명(null), 권한목록)으로 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities); // ✅ authorities 전달

                        // 4. SecurityContext에 인증 정보 저장
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    } else {
                        setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Redis에 저장된 토큰과 일치하지 않습니다.");
                        return;
                    }
                } else {
                    setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "유효하지 않은 토큰입니다.");
                    return;
                }

            } catch (ExpiredJwtException e) {
                setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인해주세요.");
                return;

            } catch (JwtException e) { // 잘못된 서명, 구조, 형식 등
                setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "잘못된 JWT 형식입니다: " + e.getMessage());
                return;

            } catch (Exception e) {
                setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JWT 인증 처리 중 오류가 발생했습니다.");
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
