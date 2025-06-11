package backend.techeerzip.domain.userExperience.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateUserExperienceRequest", description = "경력 요청 DTO")
public class CreateUserExperienceRequest {

    @NotBlank
    @Schema(description = "포지션", example = "BACKEND")
    private String position;

    @NotBlank
    @Schema(description = "회사명", example = "CrowdStrike")
    private String companyName;

    @NotNull
    @Schema(description = "경력 시작일", example = "2021-01-01")
    private LocalDate startDate;

    @Schema(description = "경력 종료일", example = "2021-06-01")
    private LocalDate endDate;

    @NotBlank
    @Schema(description = "경력 카테고리", example = "인턴")
    private String category;

    @NotNull
    @Schema(description = "종료 여부", example = "true")
    private Boolean isFinished;

    @Schema(description = "경력 설명", example = "신규 기능 개발 및 서버 관리")
    private String description;
}
