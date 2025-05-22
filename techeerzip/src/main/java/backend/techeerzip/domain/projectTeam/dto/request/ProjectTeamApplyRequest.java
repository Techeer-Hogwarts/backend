package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.projectTeam.type.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectTeamApplyRequest(
        @NotNull Long projectTeamId,
        @NotNull TeamRole teamRole,
        @NotBlank String summary) {}
