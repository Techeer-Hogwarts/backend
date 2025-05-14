package backend.techeerzip.domain.blog.dto.response;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "BlogResponse", description = "블로그 단건 조회 응답 DTO")
public class BlogResponse {

    @Schema(description = "블로그 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "Spring Boot로 만드는 REST API")
    private String title;

    @Schema(description = "URL", example = "https://techeer.io/blog/1")
    private String url;

    @Schema(description = "게시 날짜", example = "2025-05-14T09:30:00")
    private LocalDateTime date;

    @Schema(description = "카테고리", example = "TECHEER")
    private String category;

    @Schema(description = "생성 일시", example = "2025-05-14T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "좋아요 수", example = "123")
    private Integer likeCount;

    @Schema(description = "조회 수", example = "456")
    private Integer viewCount;

    @Schema(description = "썸네일 URL", example = "https://techeer.io/img/thumb.png")
    private String thumbnail;

    @Schema(description = "원본 작성자 정보")
    private Author author;

    @Schema(description = "로그인 사용자 정보(선택)")
    private BlogAuthorResponse user;

    public BlogResponse(Blog blog) {
        this.id         = blog.getId();
        this.title      = blog.getTitle();
        this.url        = blog.getUrl();
        this.date       = blog.getDate();
        this.category   = blog.getCategory();
        this.createdAt  = blog.getCreatedAt();
        this.likeCount  = blog.getLikeCount();
        this.viewCount  = blog.getViewCount();
        this.thumbnail  = blog.getThumbnail();
        this.author     = new Author(blog.getAuthor(), blog.getAuthorImage());

        User u = blog.getUser();
        this.user       = (u != null) ? new BlogAuthorResponse(u) : null;
    }

    @Data
    @Schema(name = "Author", description = "블로그 작성자 정보")
    public static class Author {
        @Schema(description = "작성자 이름", example = "홍길동")
        private final String authorName;

        @Schema(description = "작성자 이미지 URL", example = "https://techeer.io/img/user.png")
        private final String authorImage;

        public Author(String authorName, String authorImage) {
            this.authorName  = authorName;
            this.authorImage = authorImage;
        }
    }
}