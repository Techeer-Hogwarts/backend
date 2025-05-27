package backend.techeerzip.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import backend.techeerzip.domain.like.dto.response.user.ResumeUserResponse;
import java.time.LocalDateTime;

@Schema(description = "좋아요한 이력서 응답")
public record LikedResumeResponse(
    @Schema(description = "이력서 ID") Long id,
    @Schema(description = "생성일") LocalDateTime createdAt,
    @Schema(description = "수정일") LocalDateTime updatedAt,
    @Schema(description = "제목") String title,
    @Schema(description = "URL") String url,
    @Schema(description = "대표 이력서 여부") boolean isMain,
    @Schema(description = "카테고리") String category,
    @Schema(description = "포지션") String position,
    @Schema(description = "좋아요 수") Integer likeCount,
    @Schema(description = "조회수") Integer viewCount,
    @Schema(description = "사용자 정보") ResumeUserResponse user
) implements LikedContentResponse {} 