package backend.techeerzip.domain.projectTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "프로젝트 팀 수정 요청 DTO")
public class ProjectTeamUpdateRequest {

    @Schema(
            description = "변경할 팀원 목록 (팀원 ID, 역할, 리더 여부 포함)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Valid
    @Builder.Default
    private final List<ProjectMemberInfoRequest> projectMember = List.of();

    @Schema(
            description = "팀 기술 스택 목록 (스택 이름, 메인 스택 여부)",
            example = "[{\"stack\": \"React.js\", \"isMain\": true}]"
    )
    @Builder.Default
    private List<TeamStackInfo.WithName> teamStacks = List.of();

    @Schema(
            description = "삭제할 팀원 ID 목록",
            example = "[1, 2]"
    )
    @Builder.Default
    private List<Long> deleteMembers = List.of();

    @Schema(
            description = "삭제할 메인 이미지 ID 목록",
            example = "[10, 11]"
    )
    @Builder.Default
    private List<Long> deleteMainImages = List.of();

    @Schema(
            description = "삭제할 결과 이미지 ID 목록",
            example = "[21, 22]"
    )
    @Builder.Default
    private List<Long> deleteResultImages = List.of();

    @Schema(description = "팀 기본 정보 (이름, 설명, 모집 상태 등)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    @JsonUnwrapped
    private TeamData teamData;

    @Schema(description = "모집 인원 수 정보 (포지션별)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    @JsonUnwrapped
    private RecruitCounts recruitCounts;
}
