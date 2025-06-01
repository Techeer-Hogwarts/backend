package backend.techeerzip.domain.projectTeam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import backend.techeerzip.domain.projectTeam.type.TeamType;

/**
 * 팀 목록 슬라이스 응답의 공통 인터페이스입니다.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@Schema(description = "팀 슬라이스 응답 공통 필드")
public interface SliceTeamsResponse {

    @Schema(description = "팀 이름", example = "리액트 스터디")
    String getName();

    @Schema(description = "생성일", example = "2024-10-01T12:00:00")
    LocalDateTime getCreatedAt();

    @Schema(description = "최근 수정일", example = "2024-10-02T10:00:00")
    LocalDateTime getUpdatedAt();

    @Schema(description = "조회수", example = "120")
    int getViewCount();

    @Schema(description = "좋아요 수", example = "15")
    int getLikeCount();

    @Schema(description = "삭제 여부", example = "false")
    boolean isDeleted();

    @Schema(description = "모집 중 여부", example = "true")
    boolean isRecruited();

    @Schema(description = "모집 완료 여부", example = "false")
    boolean isFinished();

    @Schema(description = "팀 종류 (PROJECT 또는 STUDY)", example = "PROJECT")
    TeamType getType();
}
