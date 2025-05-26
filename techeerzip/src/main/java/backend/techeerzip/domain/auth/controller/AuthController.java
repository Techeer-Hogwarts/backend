package backend.techeerzip.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.service.AuthService;
import backend.techeerzip.global.logger.CustomLogger;
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

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        logger.info("로그인 요청 처리 중 - email: {}", loginRequest.getEmail(), CONTEXT);
        authService.login(response, loginRequest);
        logger.info("로그인 처리 완료 - email: {}", loginRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok().build();
    }
}
