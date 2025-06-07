package backend.techeerzip.domain.blog.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "BlogResponse", description = "블로그 단건 조회 응답 DTO")
public class BlogResponse {

    @Schema(description = "블로그 ID", example = "1")
    private final Long id;

    @Schema(description = "제목", example = "Spring Boot로 만드는 REST API")
    private final String title;

    @Schema(description = "URL", example = "https://techeer.io/blog/1")
    private final String url;

    @Schema(description = "게시 날짜", example = "2025-05-14T09:30:00")
    private final LocalDateTime date;

    @Schema(description = "카테고리", example = "TECHEER")
    private final String category;

    @Schema(description = "생성 일시", example = "2025-05-14T12:34:56")
    private final LocalDateTime createdAt;

    @Schema(description = "좋아요 수", example = "123")
    private final Integer likeCount;

    @Schema(description = "조회 수", example = "456")
    private final Integer viewCount;

    @Schema(description = "썸네일 URL", example = "https://techeer.io/img/thumb.png")
    private final String thumbnail;

    @Schema(description = "원본 작성자 정보")
    private final Author author;

    @Schema(description = "로그인 사용자 정보(선택)")
    private final BlogAuthorResponse user;

    @Schema(name = "Author", description = "블로그 작성자 정보")
    public record Author(
            @Schema(description = "작성자 이름", example = "홍길동") String authorName,
            @Schema(description = "작성자 이미지 URL", example = "https://techeer.io/img/user.png")
                    String authorImage) {}
}
