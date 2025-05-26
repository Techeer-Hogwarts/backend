package backend.techeerzip.domain.session.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CursorPageViewCountRequest(
        @Schema(description = "가져올 데이터 수 (기본값: 10)", example = "10", nullable = true)
        Integer size,

        @Schema(description = "마지막으로 조회된 데이터의 ID (커서)", example = "8", nullable = true)
        Long cursor,

        @Schema(description = "마지막으로 조회된 데이터의 생성일시", example = "2025-05-19T19:36:59", nullable = true)
        LocalDateTime createdAt,

        @Schema(description = "마지막으로 조회된 데이터의 조회수", example = "9", nullable = true)
        Integer viewCount
) {}
