package backend.techeerzip.domain.projectMember.dto;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectMemberInfoRequest(
        @NotNull @Schema(description = "사용자 ID", example = "42") Long userId,
        @NotNull @Schema(description = "팀 리더 여부", example = "true") Boolean isLeader,
        @NotNull @Schema(description = "프로젝트 팀 내 역할", example = "BACKEND") TeamRole teamRole) {}
