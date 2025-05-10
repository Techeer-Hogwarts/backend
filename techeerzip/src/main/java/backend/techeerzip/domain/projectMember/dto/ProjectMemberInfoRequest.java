package backend.techeerzip.domain.projectMember.dto;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.TeamRole;

public record ProjectMemberInfoRequest(
        @NotNull Long userId, @NotNull Boolean isLeader, @NotNull TeamRole teamRole) {}
