package backend.techeerzip.domain.session.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record SessionBestListResponse<T>(
        @Schema(description = "조회된 데이터 목록")
        List<T> content,

        @Schema(description = "다음 페이지 조회를 위한 커서 ID", example = "10")
        Long nextCursor,

        @Schema(description = "다음 페이지 조회를 위한 커서 생성일", example = "2024-05-01T12:00:00", nullable = true)
        LocalDateTime nextCreatedAt,

        @Schema(description = "다음 페이지 조회를 위한 커서 조회수", example = "120", nullable = true)
        Integer nextViewCount,

        @Schema(description = "다음 페이지가 존재하는지 여부", example = "true")
        boolean hasNext
) {}
