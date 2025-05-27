package backend.techeerzip.domain.like.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import backend.techeerzip.domain.like.entity.LikeCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "LikeListQueryRequest", description = "좋아요 목록 조회 요청 DTO")
public class LikeListQueryRequest {
    @Schema(
            description = "카테고리 (SESSION, BLOG, RESUME, PROJECT, STUDY)",
            example = "BLOG",
            allowableValues = {"SESSION", "BLOG", "RESUME", "PROJECT", "STUDY"})
    private LikeCategory category;

    @Schema(description = "마지막으로 조회한 좋아요의 ID", example = "0")
    @Min(value = 0, message = "cursorId는 0 이상의 정수여야 합니다.")
    private Long cursorId = 0L;

    @Schema(description = "가져올 개수", example = "10")
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit = 10;

    @Schema(
            description = "정렬 기준 (latest: 최신순, likeCount: 좋아요순, viewCount: 조회수순)",
            example = "latest",
            allowableValues = {"latest", "likeCount", "viewCount"})
    @Pattern(
            regexp = "^(latest|likeCount|viewCount)$",
            message = "정렬 기준은 latest, likeCount, viewCount 중 하나여야 합니다.")
    private String sortBy;

    public String getSortBy() {
        return sortBy == null || sortBy.trim().isEmpty() ? "latest" : sortBy;
    }
} 