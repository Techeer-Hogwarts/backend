package backend.techeerzip.domain.projectTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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
    @Schema(description = "프론트엔드 모집 인원", example = "1", minimum = "0")
    private Integer frontendNum;

    @NotNull
    @Min(0)
    @Schema(description = "백엔드 모집 인원", example = "2", minimum = "0")
    private Integer backendNum;

    @NotNull
    @Min(0)
    @Schema(description = "풀스택 모집 인원", example = "0", minimum = "0")
    private Integer fullStackNum;

    @NotNull
    @Min(0)
    @Schema(description = "DevOps 모집 인원", example = "1", minimum = "0")
    private Integer devOpsNum;

    @NotNull
    @Min(0)
    @Schema(description = "데이터 엔지니어 모집 인원", example = "1", minimum = "0")
    private Integer dataEngineerNum;
}
