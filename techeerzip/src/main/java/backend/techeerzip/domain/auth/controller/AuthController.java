package backend.techeerzip.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.dto.request.SendEmailRequest;
import backend.techeerzip.domain.auth.dto.request.VerifyCodeRequest;
import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.domain.auth.service.AuthService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;
    private final CustomLogger logger;
    private static final String CONTEXT = "AuthController";

    @PostMapping("/email")
    @Override
    public ResponseEntity<Void> sendVerificationEmail(
            @Valid @RequestBody SendEmailRequest sendEmailRequest) {
        String email = sendEmailRequest.getEmail();

        logger.info("이메일 인증 코드 전송 중 - email: {}", email, CONTEXT);
        authService.sendVerificationEmail(email);
        logger.info("이메일 인증 코드 전송 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code")
    @Override
    public ResponseEntity<Void> verifyCode(
            @Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        String email = verifyCodeRequest.getEmail();
        String code = verifyCodeRequest.getCode();

        logger.info("이메일 인증 코드 확인 요청 처리 중 - email: {}", email, CONTEXT);
        authService.verifyCode(email, code);
        logger.info("이메일 인증 코드 확인 요청 처리 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<TokenPair> login(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        logger.info("로그인 요청 처리 중 - email: {}", loginRequest.getEmail(), CONTEXT);
        TokenPair tokenPair = authService.login(response, loginRequest);
        logger.info("로그인 처리 완료 - email: {}", loginRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logout(
            @Valid @Parameter(hidden = true) @UserId Long userId, HttpServletResponse response) {
        logger.info("로그아웃 처리 중 - userId: {}", userId, CONTEXT);
        authService.logout(userId, response);
        logger.info("로그아웃 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }
}
