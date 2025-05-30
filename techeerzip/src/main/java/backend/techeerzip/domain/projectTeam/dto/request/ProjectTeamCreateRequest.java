package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;
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
public class ProjectTeamCreateRequest {

    @NotNull @Valid @JsonUnwrapped private TeamData teamData;

    @NotNull @Valid @JsonUnwrapped private RecruitCounts recruitCounts;

    @Valid @NotNull private List<ProjectMemberInfoRequest> projectMember;

    @Nullable private List<TeamStackInfo.WithName> teamStacks;
}
