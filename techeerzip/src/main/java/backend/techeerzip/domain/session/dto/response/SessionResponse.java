package backend.techeerzip.domain.session.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.session.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SessionResponse", description = "세션 단건 조회 응답 DTO")
public record SessionResponse(
        @Schema(description = "세션 ID", example = "1") Long id,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
                String thumbnail,
        @Schema(description = "세션 제목", example = "Spring Boot 기초 강의") String title,
        @Schema(description = "발표자 이름", example = "김테커") String presenter,
        @Schema(description = "세션 날짜", example = "2024-03-20") String date,
        @Schema(description = "발표자 직책", example = "BACKEND") String position,
        @Schema(description = "세션 카테고리", example = "BOOTCAMP") String category,
        @Schema(description = "비디오 URL", example = "https://example.com/video.mp4") String videoUrl,
        @Schema(description = "파일 URL", example = "https://example.com/file.pdf") String fileUrl,
        @Schema(description = "좋아요 수", example = "42") Integer likeCount,
        @Schema(description = "조회수", example = "1000") Integer viewCount,
        @Schema(description = "생성일", example = "2025-02-25T07:48:06.558") LocalDateTime createdAt,
        @Schema(description = "수정일", example = "2025-04-09T11:38:50.297") LocalDateTime updatedAt,
        @Schema(description = "사용자 정보") UserInfo user) {
    public static SessionResponse from(Session session) {
        Long userId = null;
        UserInfo userInfo = null;
        if (session.getUser() != null) {
            userId = session.getUser().getId();
            userInfo =
                    new UserInfo(
                            session.getUser().getName(),
                            session.getUser().getNickname(),
                            session.getUser().getProfileImage());
        }

        return new SessionResponse(
                session.getId(),
                userId,
                session.getThumbnail(),
                session.getTitle(),
                session.getPresenter(),
                session.getDate(),
                session.getPosition(),
                session.getCategory(),
                session.getVideoUrl(),
                session.getFileUrl(),
                session.getLikeCount(),
                session.getViewCount(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                userInfo);
    }

    @Schema(name = "UserInfo", description = "사용자 정보")
    public record UserInfo(
            @Schema(description = "사용자 이름", example = "김테커") String name,
            @Schema(description = "사용자 닉네임", example = "Techeer") String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
                    String profileImage) {}
}
