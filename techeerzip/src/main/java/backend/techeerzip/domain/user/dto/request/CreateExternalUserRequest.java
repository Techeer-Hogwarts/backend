package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import backend.techeerzip.domain.user.entity.JoinReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExternalUserRequest {
    @NotBlank
    @Schema(description = "이름", example = "김테커")
    private String name;

    @Email
    @NotBlank
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "비밀번호", example = "Passw0rd!")
    private String password;

    @NotBlank
    @Schema(description = "가입 동기", example = "BOOTCAMP")
    private JoinReason joinReason;
}
