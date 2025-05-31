package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateUserRequest", description = "사용자 DTO")
public class CreateUserRequest {

    @NotBlank
    @Schema(description = "메인 포지션", example = "BACKEND")
    private String mainPosition;

    @Schema(description = "서브 포지션", example = "FRONTEND")
    private String subPosition;

    @NotBlank
    @Schema(description = "이름", example = "김테커")
    private String name;

    @NotBlank
    @Schema(description = "GitHub URL", example = "https://github.com/username")
    private String githubUrl;

    @Schema(description = "Tistory URL", example = "https://tistory.com")
    private String tistoryUrl;

    @NotNull
    @Schema(description = "LFT 여부", example = "false")
    private Boolean isLft;

    @NotBlank
    @Schema(description = "학교명", example = "인천대학교")
    private String school;

    @NotBlank
    @Schema(description = "학년", example = "1학년")
    private String grade;

    @Schema(description = "Medium URL", example = "https://medium.com")
    private String mediumUrl;

    @Schema(description = "Velog URL", example = "https://velog.io")
    private String velogUrl;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "비밀번호", example = "Passw0rd!")
    private String password;

    @Email
    @NotBlank
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    @Min(1)
    @Schema(description = "기수 (1 이상)", example = "6")
    private Integer year;
}
