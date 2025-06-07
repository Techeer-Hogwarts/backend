package backend.techeerzip.domain.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "BlogAuthorResponse", description = "블로그 작성자(유저) 정보 응답 DTO")
public class BlogAuthorResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long id;

    @Schema(description = "이름", example = "홍길동")
    private final String name;

    @Schema(description = "프로필 이미지 URL", example = "https://techeer.io/img/profile.png")
    private final String profileImage;
}
