package backend.techeerzip.domain.bookmark.dto.response;

import backend.techeerzip.domain.like.dto.response.user.SessionUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크한 세션 응답")
public record BookmarkedSessionResponse(
        @Schema(description = "세션 ID") Long id,
        @Schema(description = "유저 ID") Long userId,
        @Schema(description = "썸네일 URL") String thumbnail,
        @Schema(description = "제목") String title,
        @Schema(description = "발표자") String presenter,
        @Schema(description = "날짜") String date,
        @Schema(description = "포지션") String position,
        @Schema(description = "카테고리") String category,
        @Schema(description = "비디오 URL") String videoUrl,
        @Schema(description = "파일 URL") String fileUrl,
        @Schema(description = "좋아요 수") Integer likeCount,
        @Schema(description = "조회수") Integer viewCount,
        @Schema(description = "유저 정보") SessionUserResponse user)
        implements BookmarkedContentResponse {}
