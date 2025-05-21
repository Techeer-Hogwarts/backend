package backend.techeerzip.domain.blog.dto.request;

import java.time.format.DateTimeFormatter;
import java.util.List;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BlogIndexRequest", description = "블로그 인덱싱 요청 DTO")
public class BlogIndexRequest {

    @Schema(description = "게시 날짜 (ISO 8601 문자열)", example = "2025-05-14T09:30:00")
    private String date;

    @Schema(description = "블로그 ID", example = "1")
    private String id;

    @Schema(description = "태그 목록", example = "[\"java\",\"spring\"]")
    private List<String> stack;

    @Schema(description = "썸네일 URL", example = "https://techeer.io/img/thumb.png")
    private String thumbnail;

    @Schema(description = "제목", example = "Spring Boot로 만드는 REST API")
    private String title;

    @Schema(description = "URL", example = "https://techeer.io/blog/1")
    private String url;

    @Schema(description = "사용자 ID", example = "2")
    private String userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "사용자 프로필 이미지 URL", example = "https://techeer.io/img/profile.png")
    private String userProfileImage;

    public BlogIndexRequest(Blog blog) {
        this.date = blog.getDate().format(DateTimeFormatter.ISO_DATE_TIME);
        this.id = blog.getId().toString();
        this.stack = blog.getTags();
        this.thumbnail = blog.getThumbnail();
        this.title = blog.getTitle();
        this.url = blog.getUrl();

        User user = blog.getUser();
        this.userId = user.getId().toString();
        this.userName = user.getName();
        this.userProfileImage = user.getProfileImage();
    }
}
