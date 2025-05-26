package backend.techeerzip.domain.user.controller;

import backend.techeerzip.domain.user.dto.request.CreateUserWithResumeRequest;
import backend.techeerzip.domain.user.dto.request.UserResetPasswordRequest;
import backend.techeerzip.domain.user.service.UserService;
import backend.techeerzip.global.logger.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user", description = "유저 API")
@RestController
@RequestMapping("/api/v3/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserController";

    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody CreateUserWithResumeRequest createUserWithResumeRequest,
            HttpServletResponse response) {
        logger.info(
                "회원가입 요청 처리 중 - email: {}",
                createUserWithResumeRequest.getCreateUserRequest().getEmail(),
                CONTEXT);
        logger.info(
                "회원가입 처리 완료 - email: {}",
                createUserWithResumeRequest.getCreateUserRequest().getEmail(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 비밀번호를 재설정합니다.")
    @PatchMapping("/findPwd")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody UserResetPasswordRequest userResetPasswordRequest,
            HttpServletResponse response) {
        String email = userResetPasswordRequest.getEmail();
        String code = userResetPasswordRequest.getCode();
        String newPassword = userResetPasswordRequest.getNewPassword();

        userService.resetPassword(email, code, newPassword);
        logger.info("비밀번호 재설정 요청 처리 완료 - email: {}", userResetPasswordRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok().build();
    }
}
