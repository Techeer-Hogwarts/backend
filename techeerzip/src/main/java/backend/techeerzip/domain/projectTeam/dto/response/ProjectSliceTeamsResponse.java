package backend.techeerzip.domain.projectTeam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonTypeName("PROJECT")
@Schema(description = "프로젝트 팀 정보 (전체 목록 응답용)", name = "ProjectSliceTeamsResponse")
public class ProjectSliceTeamsResponse implements SliceTeamsResponse {

    private final Long id;
    private final String name;
    private final String projectExplain;
    private final boolean isDeleted;
    private final boolean isFinished;
    private final boolean isRecruited;
    private final int frontendNum;
    private final int backendNum;
    private final int devopsNum;
    private final int fullStackNum;
    private final int dataEngineerNum;
    private final int viewCount;
    private final int likeCount;
    private final List<String> mainImages;
    private final List<TeamStackInfo.WithName> teamStacks;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final TeamType type = TeamType.PROJECT;
}
