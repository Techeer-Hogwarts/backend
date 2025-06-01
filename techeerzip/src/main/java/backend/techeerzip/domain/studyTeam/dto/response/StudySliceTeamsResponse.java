package backend.techeerzip.domain.studyTeam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeName;

import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonTypeName("STUDY")
@Schema(description = "스터디 팀 카드 정보 (전체 목록 응답용)", name = "StudySliceTeamsResponse")
public class StudySliceTeamsResponse implements SliceTeamsResponse {

    @Schema(description = "스터디 팀 ID", example = "1")
    private final Long id;

    @Schema(description = "스터디 이름", example = "CS 스터디")
    private final String name;

    @Schema(description = "스터디 설명", example = "컴퓨터 공학 지식을 심화 학습하는 스터디입니다.")
    private final String studyExplain;

    @Schema(description = "삭제 여부", example = "false")
    private final boolean isDeleted;

    @Schema(description = "스터디 종료 여부", example = "false")
    private final boolean isFinished;

    @Schema(description = "모집 중 여부", example = "true")
    private final boolean isRecruited;

    @Schema(description = "모집 인원 수", example = "5")
    private final int recruitNum;

    @Schema(description = "좋아요 수", example = "12")
    private final int likeCount;

    @Schema(description = "조회 수", example = "124")
    private final int viewCount;

    @Schema(description = "최근 수정 시각", example = "2025-05-30T22:13:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "생성 시각", example = "2025-05-15T10:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "팀 타입 (고정값: STUDY)", example = "STUDY", allowableValues = {"STUDY"})
    private final TeamType type = TeamType.STUDY;
}
