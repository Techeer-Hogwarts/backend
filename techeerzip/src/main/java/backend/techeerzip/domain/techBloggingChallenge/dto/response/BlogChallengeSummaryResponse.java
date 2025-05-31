package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BlogChallengeSummaryResponse", description = "회차별 블로그 요약 응답 DTO")
public class BlogChallengeSummaryResponse {
    @Schema(description = "블로그 ID", example = "101")
    private Long blogId;

    @Schema(description = "제목", example = "블로그 제목")
    private String title;

    @Schema(description = "URL", example = "https://...")
    private String url;

    @Schema(description = "작성자", example = "홍길동")
    private String author;

    @Schema(description = "작성일시", example = "2025-05-27T14:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "조회수", example = "123")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "10")
    private Integer likeCount;
}
