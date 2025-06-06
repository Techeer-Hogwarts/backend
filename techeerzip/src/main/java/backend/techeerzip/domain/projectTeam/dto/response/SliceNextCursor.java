package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 커서 기반 슬라이스 페이징에서 다음 페이지 조회를 위한 커서 정보 DTO입니다.
 *
 * <p>응답 페이지의 마지막 요소 기준으로 다음 요청 시 필요한 커서 값을 전달합니다.
 *
 * <ul>
 *   <li>{@code hasNext} → 다음 페이지가 존재하면 true
 *   <li>{@code sortType}에 따라 커서 필드는 달라집니다:
 *       <ul>
 *         <li>{@code UPDATE_AT_DESC} → {@code dateCursor}, {@code id}
 *         <li>{@code VIEW_COUNT_DESC}, {@code LIKE_COUNT_DESC} → {@code countCursor}, {@code id}
 *       </ul>
 * </ul>
 */
@Schema(description = "슬라이스 페이징을 위한 커서 정보")
@Getter
@Builder
public class SliceNextCursor {

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private final Boolean hasNext;

    @Schema(description = "마지막 요소의 ID", example = "101")
    private final Long id;

    @Schema(description = "커서 기준 날짜 (updatedAt 등)", example = "2024-10-01T12:00:00")
    private final LocalDateTime dateCursor;

    @Schema(description = "커서 기준 정수값 (조회수, 좋아요 수 등)", example = "150")
    private final Integer countCursor;

    @Schema(description = "정렬 기준 타입", example = "UPDATE_AT_DESC")
    private final SortType sortType;
}
