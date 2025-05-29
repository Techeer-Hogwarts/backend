package backend.techeerzip.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import backend.techeerzip.domain.auth.jwt.JwtAuthenticationFilter;
import backend.techeerzip.domain.auth.jwt.JwtTokenProvider;
import backend.techeerzip.domain.auth.jwt.handler.CustomAccessDeniedHandler;
import backend.techeerzip.domain.auth.jwt.handler.CustomAuthenticationEntryPoint;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomLogger logger;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // 스웨거 필터 먼저 거쳐서 basic 인증
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher(
                        "api/v3/api-docs/**",
                        "/api/v3/docs/**",
                        "/api/v3/docs",
                        "/api/v3/swagger-ui/**",
                        "/api/v3/swagger-resources/**",
                        "/api/v3/swagger-ui/**",
                        "/api/v3/swagger-ui.html",
                        "/api/v3/webjars/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.realmName("Swagger API Documentation"))
                .csrf(csrf -> csrf.disable())
                .build();
    }

    // jwt 인증
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // jwt 인증 필요 없는 엔드 포인트
                                        .requestMatchers("api/v3/auth/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, logger),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
