package backend.techeerzip.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.dto.request.SendEmailRequest;
import backend.techeerzip.domain.auth.dto.request.VerifyCodeRequest;
import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "auth", description = "Auth API")
public interface AuthSwagger {
    @Operation(summary = "이메일 인증 코드 전송", description = "사용자의 이메일로 인증 코드를 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이메일 인증 코드 전송 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "테커가 아닌 사용자",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> sendVerificationEmail(
            @Valid @Parameter(description = "이메일 정보") SendEmailRequest sendEmailRequest) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "전송된 이메일 인증 코드를 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이메일 인증 코드 확인 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "이메일 인증 코드 불일치",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> verifyCode(
            @Valid @Parameter(description = "이메일 및 인증 코드") VerifyCodeRequest verifyCodeRequest) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = TokenPair.class)))
    default ResponseEntity<TokenPair> login(
            @Valid @Parameter(description = "로그인 요청 정보") LoginRequest loginRequest,
            HttpServletResponse response) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    default ResponseEntity<Void> logout(
            @Parameter(hidden = true) Long userId, HttpServletResponse response) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }
}
