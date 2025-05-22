package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.TeamRole;

public record ProjectTeamApplyRequest(
        @NotNull Long projectTeamId, @NotNull TeamRole teamRole, @NotBlank String summary) {}
