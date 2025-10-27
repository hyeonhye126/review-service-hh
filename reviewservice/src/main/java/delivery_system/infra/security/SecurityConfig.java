package delivery_system.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ReviewService Security 설정
 *
 * 공개 API (인증 불필요):
 * - GET /api/v1/reviews/store/{storeId} - 가게의 리뷰 목록 조회
 * - GET /api/v1/reviews/order/{orderId} - 주문의 리뷰 조회
 *
 * 인증 필요 API:
 * - POST /api/v1/reviews/{storeId}/orders/{orderId} - 리뷰 작성
 * - GET /api/v1/reviews - 내 리뷰 목록 조회
 * - PUT /api/v1/reviews/{reviewId} - 리뷰 수정 (작성자만)
 * - DELETE /api/v1/reviews/{reviewId} - 리뷰 삭제 (CUSTOMER/MANAGER/MASTER)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🟢 공개 API (인증 불필요)
                        .requestMatchers(
                                "/actuator/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/v1/reviews/store/**",
                                "/api/v1/reviews/order/**"
                        ).permitAll()
                        // 🔐 나머지 API는 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}