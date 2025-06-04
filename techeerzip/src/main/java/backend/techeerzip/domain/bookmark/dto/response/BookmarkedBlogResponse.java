package backend.techeerzip.domain.bookmark.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.like.dto.response.author.BlogAuthorResponse;
import backend.techeerzip.domain.like.dto.response.user.BlogUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크한 블로그 응답")
public record BookmarkedBlogResponse(
        @Schema(description = "블로그 ID") Long id,
        @Schema(description = "제목") String title,
        @Schema(description = "URL") String url,
        @Schema(description = "날짜") LocalDateTime date,
        @Schema(description = "카테고리") String category,
        @Schema(description = "생성일") LocalDateTime createdAt,
        @Schema(description = "좋아요 수") Integer likeCount,
        @Schema(description = "조회수") Integer viewCount,
        @Schema(description = "썸네일 URL") String thumbnail,
        @Schema(description = "작성자 정보") BlogAuthorResponse author,
        @Schema(description = "유저 정보") BlogUserResponse user)
        implements BookmarkedContentResponse {}
