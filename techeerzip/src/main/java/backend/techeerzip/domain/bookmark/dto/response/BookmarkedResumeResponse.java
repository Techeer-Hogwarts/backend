package backend.techeerzip.domain.bookmark.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.like.dto.response.user.ResumeUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크한 이력서 응답")
public record BookmarkedResumeResponse(
        @Schema(description = "이력서 ID") Long id,
        @Schema(description = "생성일") LocalDateTime createdAt,
        @Schema(description = "수정일") LocalDateTime updatedAt,
        @Schema(description = "제목") String title,
        @Schema(description = "URL") String url,
        @Schema(description = "대표 이력서 여부") boolean isMain,
        @Schema(description = "카테고리") String category,
        @Schema(description = "포지션") String position,
        @Schema(description = "좋아요 수") Integer likeCount,
        @Schema(description = "조회수") Integer viewCount,
        @Schema(description = "사용자 정보") ResumeUserResponse user)
        implements BookmarkedContentResponse {}
