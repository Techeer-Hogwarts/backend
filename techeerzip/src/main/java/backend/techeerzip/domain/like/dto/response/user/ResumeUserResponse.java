package backend.techeerzip.domain.like.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이력서 사용자 응답")
public record ResumeUserResponse(
        @Schema(description = "사용자 ID") Long id,
        @Schema(description = "이름") String name,
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "프로필 이미지") String profileImage,
        @Schema(description = "기수") Integer year,
        @Schema(description = "주 포지션") String mainPosition,
        @Schema(description = "부 포지션") String subPosition,
        @Schema(description = "학교") String school,
        @Schema(description = "학년") String grade,
        @Schema(description = "이메일") String email,
        @Schema(description = "GitHub URL") String githubUrl,
        @Schema(description = "Medium URL") String mediumUrl,
        @Schema(description = "Tistory URL") String tistoryUrl,
        @Schema(description = "Velog URL") String velogUrl,
        @Schema(description = "역할 ID") Long roleId) {}
