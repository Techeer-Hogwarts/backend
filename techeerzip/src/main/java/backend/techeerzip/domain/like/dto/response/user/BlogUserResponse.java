package backend.techeerzip.domain.like.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "블로그 사용자 응답")
public record BlogUserResponse(
    @Schema(description = "사용자 ID") Long id,
    @Schema(description = "이름") String name,
    @Schema(description = "닉네임") String nickname,
    @Schema(description = "역할 ID") Long roleId,
    @Schema(description = "프로필 이미지") String profileImage
) {} 