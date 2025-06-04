package backend.techeerzip.domain.studyTeam.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디 팀 생성 요청 DTO")
public class StudyTeamCreateRequest {

    @NotNull
    @Valid
    @JsonUnwrapped
    @Schema(description = "스터디 팀 기본 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private StudyData studyData;

    @NotNull
    @Valid
    @Schema(
            description = "스터디 팀원 목록",
            example = "[{\"userId\": 1, \"isLeader\": true}, {\"userId\": 2, \"isLeader\": false}]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<StudyMemberInfoRequest> studyMember;
}
