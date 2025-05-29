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
        http.securityMatcher("/api/v3/docs/**", "/api/v3/swagger-ui/**", "/api/v3/api-docs/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.realmName("Swagger API Documentation"))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // jwt 인증
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        auth ->
                                auth
                                        // jwt 인증 필요 없는 엔드 포인트
                                        .requestMatchers(
                                                "/api/v3/auth/email",
                                                "/api/v3/auth/code",
                                                "/api/v3/auth/login",
                                                "/api/v3/users/signup",
                                                "/api/v3/users/findPwd")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, logger),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
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
