package backend.techeerzip.domain.bookmark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "북마크한 콘텐츠 응답")
public sealed interface BookmarkedContentResponse
        permits BookmarkedBlogResponse, BookmarkedSessionResponse, BookmarkedResumeResponse {

    @Schema(description = "콘텐츠 ID")
    Long id();
}
