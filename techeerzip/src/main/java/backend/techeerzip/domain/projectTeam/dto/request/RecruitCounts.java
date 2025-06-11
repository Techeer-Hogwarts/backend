package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitCounts {

    @NotNull
    @Min(0)
    @Builder.Default
    @Schema(description = "프론트엔드 모집 인원", example = "1", minimum = "0")
    private Integer frontendNum = 0; // <--- 기본값 0 설정

    @NotNull
    @Min(0)
    @Builder.Default
    @Schema(description = "백엔드 모집 인원", example = "2", minimum = "0")
    private Integer backendNum = 0; // <--- 기본값 0 설정

    @NotNull
    @Min(0)
    @Builder.Default
    @Schema(description = "풀스택 모집 인원", example = "0", minimum = "0")
    private Integer fullStackNum = 0;

    @NotNull
    @Min(0)
    @Builder.Default
    @Schema(description = "DevOps 모집 인원", example = "1", minimum = "0")
    private Integer devOpsNum = 0; // <--- 기본값 0 설정

    @NotNull
    @Min(0)
    @Builder.Default
    @Schema(description = "데이터 엔지니어 모집 인원", example = "1", minimum = "0")
    private Integer dataEngineerNum = 0;
}
