package backend.techeerzip.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "VerifyCodeRequest", description = "이메일 인증 코드 확인 DTO")
public class VerifyCodeRequest {

    @Email
    @NotBlank
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "이메일 인증 코드", example = "123456")
    private String code;
}
