package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateUserProfileImgRequest {
    @Email
    @NotBlank
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;
}
