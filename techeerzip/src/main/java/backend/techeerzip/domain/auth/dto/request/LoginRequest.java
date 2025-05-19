package backend.techeerzip.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginRequest", description = "로그인 요청 DTO")
public class LoginRequest {

    @Email
    @NotBlank
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "passW0rd!")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
