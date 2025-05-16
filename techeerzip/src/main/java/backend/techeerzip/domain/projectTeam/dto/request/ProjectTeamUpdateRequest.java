package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamUpdateRequest {

    @NotNull @Valid @Builder.Default
    private final List<ProjectMemberInfoRequest> projectMember = List.of();

    @Builder.Default private final List<TeamStackInfo.WithName> teamStacks = List.of();
    @Builder.Default private final List<Long> deleteMembers = List.of();
    @Builder.Default private final List<Long> deleteMainImages = List.of();
    @Builder.Default private final List<Long> deleteResultImages = List.of();
    @NotNull @Valid @JsonUnwrapped private TeamData teamData;
    @NotNull @Valid @JsonUnwrapped private RecruitCounts recruitCounts;
}
