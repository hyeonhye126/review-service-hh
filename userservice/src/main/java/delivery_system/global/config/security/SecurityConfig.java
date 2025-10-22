package delivery_system.global.config.security;

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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 사용에 필수
@RequiredArgsConstructor // final 필드 생성자를 Lombok이 만듭니다.
public class SecurityConfig {

    // ✅ [핵심] @Component로 등록된 필터들을 "주입" 받습니다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final IpRateLimitFilter ipRateLimitFilter;
    // ⛔️ 'new'로 생성하는 로직은 모두 제거합니다.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
// 📢 1. CSRF 보호 비활성화 (REST API의 표준)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // 2. [핵심] 401 오류의 원인인 Basic 인증 비활성화
                // 3. [핵심] JSESSIONID 생성을 막기 위해 세션 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. 주입받은 필터 인스턴스를 등록
                .addFilterBefore(ipRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. URL 경로별 권한 설정 (순서 중요!)
                .authorizeHttpRequests(auth -> auth
                        // (허용할 경로)
                        .requestMatchers("/users/signup", "/users/login").permitAll()
                        .requestMatchers("/users/swagger-ui/**", "/users/v3/api-docs/**", "/users/swagger-ui.html").permitAll()

                        // (인증이 필요한 경로)
                        .requestMatchers("/categories/**").authenticated()
                        .requestMatchers("/stores/**").authenticated() // ⬅️ 가게 API도 추가

                        // (그 외 모든 경로는 인증 필요 - 항상 마지막에!)
                        .anyRequest().authenticated()
                )
                .build();
    }
}