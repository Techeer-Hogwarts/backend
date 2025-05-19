package backend.techeerzip.domain.auth.jwt;

import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.global.logger.CustomLogger;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomLogger logger;

    private static final String CONTEXT = "JwtAuthenticationFilter";

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    // Swagger 경로는 필터 제외
    private static final List<String> EXCLUDED_PATH_PREFIXES = List.of(
            "/api/v3/docs/**",
            "/api/v3/docs",
            "/api/v3/api-docs",
            "/api/v3/api-docs/swagger-config",
            "/api/v3/swagger-ui/**",
            "/api/v3/swagger-resources/**",
            "/api/v3/swagger-ui/**",
            "/api/v3/swagger-ui.html",
            "/api/v3/webjars/**"
    );

    private boolean isExcludedPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDED_PATH_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isExcludedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
        String refreshToken = getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);

        try {
            if (accessToken != null) {
                if (jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info(String.format("액세스 토큰 인증 완료 - email: %s", authentication.getName()), CONTEXT);
                }
            } else {
                // 액세스 토큰이 null인 경우
                reissueWithRefreshToken(refreshToken, response);
            }
        } catch (ExpiredJwtException e) {
            logger.info(String.format("액세스 토큰 만료 - email: %s", e.getClaims().getSubject()), CONTEXT);
            reissueWithRefreshToken(refreshToken, response);
        }

        filterChain.doFilter(request, response);
    }

    private void reissueWithRefreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info(String.format("새로운 액세스 토큰 발급 - email: %s", authentication.getName()), CONTEXT);

            // 새로운 액세스 토큰 발급
            TokenPair tokenPair = jwtTokenProvider.generateTokenPair(authentication);

            ResponseCookie newAccessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, tokenPair.getAccessToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 60)
                    .sameSite("None")
                    .build();


            response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}