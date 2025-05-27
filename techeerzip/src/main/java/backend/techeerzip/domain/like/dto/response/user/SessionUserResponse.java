package backend.techeerzip.domain.like.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 유저 응답")
public record SessionUserResponse(
    @Schema(description = "이름") String name,
    @Schema(description = "닉네임") String nickname,
    @Schema(description = "프로필 이미지 URL") String profileImage
) {} 