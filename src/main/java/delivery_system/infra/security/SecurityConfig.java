package delivery_system.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 - ReviewService용
 * JWT 기반 인증 구성
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize 사용 가능
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //private final IpRateLimitFilter ipRateLimitFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1. HTTP Basic 인증 비활성화 (REST API에서 불필요)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. CSRF 보호 비활성화 (REST API는 Stateless이므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. 세션 비활성화 (JWT 사용하므로 JSESSIONID 불필요)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. 필터 등록 (순서 중요!)
                //.addFilterBefore(ipRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. URL 경로별 권한 설정 (ReviewService 맞춤)
                .authorizeHttpRequests(auth -> auth
                        // ✅ 누구나 접근 가능한 경로
                        .requestMatchers("/users/signup", "/users/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // ✅ 공개 리뷰 조회 (인증 불필요)
                        .requestMatchers("/api/v1/stores/*/reviews").permitAll()
                        .requestMatchers("/api/v1/orders/*/reviews").permitAll()

                        // ✅ 인증이 필요한 경로
                        .requestMatchers("/api/v1/stores/*/orders/*/reviews").authenticated()  // 리뷰 작성
                        .requestMatchers("/api/v1/reviews/**").authenticated()                   // 리뷰 수정/삭제

                        // ✅ 관리자(MASTER)만 접근 가능
                        .requestMatchers("/api/v1/admin/reviews/**").hasRole("MASTER")

                        // ✅ 그 외 모든 경로는 인증 필요
                        .anyRequest().authenticated()
                )
                .build();
    }
}