package backend.techeerzip.domain.studyTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디 팀 수정 요청 DTO")
public class StudyTeamUpdateRequest {

    @NotNull
    @Valid
    @JsonUnwrapped
    @Schema(description = "스터디 팀 기본 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private StudyData studyData;

    @NotNull
    @Valid
    @Builder.Default
    @Schema(
            description = "스터디 멤버 정보 목록",
            example = "[{\"userId\": 1, \"isLeader\": true}, {\"userId\": 2, \"isLeader\": false}]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<StudyMemberInfoRequest> studyMember = new ArrayList<>();

    @Builder.Default
    @Schema(
            description = "삭제할 결과 이미지 ID 목록",
            example = "[10, 12]"
    )
    private List<Long> deleteImages = new ArrayList<>();

    @Builder.Default
    @Schema(
            description = "삭제할 기존 스터디 멤버 ID 목록",
            example = "[3, 4]"
    )
    private List<Long> deleteMembers = new ArrayList<>();
}
