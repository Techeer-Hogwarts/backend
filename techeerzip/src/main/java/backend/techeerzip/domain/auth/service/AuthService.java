package backend.techeerzip.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.domain.auth.exception.AuthInvalidCredentialsException;
import backend.techeerzip.domain.auth.jwt.JwtTokenProvider;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomLogger logger;
    private static final String CONTEXT = "AuthService";

    public void login(HttpServletResponse response, LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        logger.debug(String.format("로그인 요청 - email: %s", email), CONTEXT);

        try {
            Authentication authentication =
                    authenticationManager.authenticate(loginRequest.toAuthentication());

            TokenPair tokenPair = jwtTokenProvider.generateTokenPair(authentication);

            ResponseCookie accessToken =
                    ResponseCookie.from("access_token", tokenPair.getAccessToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60 * 60)
                            .sameSite("None")
                            .build();

            ResponseCookie refreshToken =
                    ResponseCookie.from("refresh_token", tokenPair.getRefreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60 * 60 * 24 * 7)
                            .sameSite("None")
                            .build();

            response.addHeader("Set-Cookie", accessToken.toString());
            response.addHeader("Set-Cookie", refreshToken.toString());

        } catch (Exception e) {
            logger.warn(String.format("로그인 실패 - email: %s", email), CONTEXT);
            throw new AuthInvalidCredentialsException();
        }
    }
}
