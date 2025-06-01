package backend.techeerzip.domain.projectTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.TeamRole;

public record ProjectTeamApplyRequest(
        @Schema(description = "프로젝트 팀 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long projectTeamId,

        @Schema(
                description = "지원 역할 (FRONTEND, BACKEND, DEVOPS, DATA_ENGINEER, FULLSTACK)",
                example = "BACKEND",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        TeamRole teamRole,

        @Schema(description = "간단한 자기소개 또는 지원 이유", example = "백엔드 경험이 있어 지원합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String summary
) {}
