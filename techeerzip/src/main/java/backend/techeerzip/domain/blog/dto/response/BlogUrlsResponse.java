package backend.techeerzip.domain.blog.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "BlogUrlsResponse", description = "블로그 URL 목록 응답 DTO")
public class BlogUrlsResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(
            description = "블로그 URL 리스트",
            example = "[\"https://example.com/blog1\", \"https://example.com/blog2\"]")
    private List<String> blogUrls;
}
