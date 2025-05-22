package backend.techeerzip.domain.blog.dto.response;

import backend.techeerzip.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BlogAuthorResponse", description = "블로그 작성자(유저) 정보 응답 DTO")
public class BlogAuthorResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "gildong")
    private String nickname;

    @Schema(description = "역할 ID", example = "3")
    private Long roleId;

    @Schema(description = "프로필 이미지 URL", example = "https://techeer.io/img/profile.png")
    private String profileImage;

    public BlogAuthorResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.roleId = user.getRole().getId();
        this.profileImage = user.getProfileImage();
    }
}
