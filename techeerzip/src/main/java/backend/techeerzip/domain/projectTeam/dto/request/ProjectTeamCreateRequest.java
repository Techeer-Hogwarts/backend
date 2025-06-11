package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamCreateRequest {

    @NotNull
    @Valid
    @JsonUnwrapped
    @Schema(description = "팀 정보", implementation = TeamData.class)
    private TeamData teamData;

    @NotNull
    @Valid
    @JsonUnwrapped
    @Schema(description = "모집 인원 수", implementation = RecruitCounts.class)
    private RecruitCounts recruitCounts;

    @Valid
    @NotNull
    @Schema(description = "프로젝트 구성원 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProjectMemberInfoRequest> projectMember;

    @Nullable
    @ArraySchema(
            schema = @Schema(implementation = TeamStackInfo.WithName.class),
            arraySchema = @Schema(description = "팀 기술 스택 리스트 (선택)"))
    private List<TeamStackInfo.WithName> teamStacks;
}
