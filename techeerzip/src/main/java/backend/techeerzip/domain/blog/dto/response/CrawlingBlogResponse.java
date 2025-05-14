package backend.techeerzip.domain.blog.dto.response;

import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "CrawlingBlogResponse", description = "크롤링된 블로그 정보 응답 DTO")
public class CrawlingBlogResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "블로그 URL", example = "https://techeer.io/blog/myblog")
    private String blogUrl;

    @Schema(description = "크롤링된 포스트 리스트")
    private List<BlogSaveRequest> posts;

    @Schema(description = "카테고리", example = "TECHEER")
    private BlogCategory category;

    public CrawlingBlogResponse(
            Long userId,
            String blogUrl,
            List<BlogSaveRequest> posts,
            String category
    ) {
        this.userId   = userId;
        this.blogUrl  = blogUrl;
        this.posts    = posts;
        this.category = BlogCategory.valueOf(category);
    }


    /**
     * taskData(JSON)와 enum 타입 Category로 바로 생성할 수 있도록 오버로드
     */
    public CrawlingBlogResponse(String taskData, BlogCategory category) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(taskData);
            this.userId   = node.get("userId").asLong();
            this.blogUrl  = node.get("blogUrl").asText();
            this.posts    = mapper.convertValue(
                    node.get("posts"),
                    new TypeReference<List<BlogSaveRequest>>() {}
            );
            this.category = category;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid taskData JSON", e);
        }
    }

    /**
     * 필터링된 포스트 목록으로 대체할 때 사용
     */
    public void updatePosts(List<BlogSaveRequest> filteredPosts) {
        this.posts = filteredPosts;
    }
}
