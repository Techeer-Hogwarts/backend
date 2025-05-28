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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "auth", description = "Auth API")
@RestController
@RequestMapping("/api/v3/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CustomLogger logger;
    private static final String CONTEXT = "AuthController";

    @Operation(summary = "이메일 인증 코드 전송", description = "사용자의 이메일로 인증 코드를 전송합니다.")
    @PostMapping("/email")
    public ResponseEntity<Void> sendVerificationEmail(
            @Valid @RequestBody SendEmailRequest sendEmailRequest, HttpServletResponse response) {
        String email = sendEmailRequest.getEmail();
        authService.sendVerificationEmail(email);
        logger.info("이메일 인증 코드 전송 중 - email: {}", email, CONTEXT);
        logger.info("이메일 인증 코드 전송 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "전송된 이메일 인증 코드를 확인합니다.")
    @PostMapping("/code")
    public ResponseEntity<Void> verifyCode(
            @Valid @RequestBody VerifyCodeRequest verifyCodeRequest, HttpServletResponse response) {
        String email = verifyCodeRequest.getEmail();
        String code = verifyCodeRequest.getCode();

        logger.info("이메일 인증 코드 확인 중 - email: {}", email, CONTEXT);

        authService.verifyCode(email, code);

        logger.info("이메일 인증 코드 확인 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        logger.info("로그인 요청 처리 중 - email: {}", loginRequest.getEmail(), CONTEXT);
        TokenPair tokenPair = authService.login(response, loginRequest);
        logger.info("로그인 처리 완료 - email: {}", loginRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok(tokenPair);
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@UserId Long userId, HttpServletResponse response) {
        logger.info("로그아웃 요청 처리 중 - userId: {}", userId, CONTEXT);
        logger.info("로그아웃 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }
}
