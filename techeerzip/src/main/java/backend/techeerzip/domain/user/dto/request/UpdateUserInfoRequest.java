package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UpdateUserInfoRequest", description = "사용자 정보 DTO")
public class UpdateUserInfoRequest {

    @Min(1)
    @Schema(description = "기수 (1 이상)", example = "6")
    private Integer year;

    @NotNull
    @Schema(description = "LFT 여부", example = "false")
    private Boolean isLft;

    @NotBlank
    @Schema(description = "학교명", example = "인천대학교")
    private String school;

    @NotBlank
    @Schema(description = "학년", example = "1학년")
    private String grade;

    @NotBlank
    @Schema(description = "메인 포지션", example = "BACKEND")
    private String mainPosition;

    @Schema(description = "서브 포지션", example = "FRONTEND")
    private String subPosition;

    @NotBlank
    @Schema(description = "GitHub URL", example = "https://github.com/username")
    private String githubUrl;

    @Schema(description = "Tistory URL", example = "https://tistory.com")
    private String tistoryUrl;

    @Schema(description = "Medium URL", example = "https://medium.com")
    private String mediumUrl;

    @Schema(description = "Velog URL", example = "https://velog.io")
    private String velogUrl;
}
