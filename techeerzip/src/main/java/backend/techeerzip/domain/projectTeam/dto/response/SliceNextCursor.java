package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.SortType;
import lombok.Builder;
import lombok.Getter;

/**
 * 커서 기반 슬라이스 페이징에서 다음 페이지 조회를 위한 커서 정보 DTO입니다.
 *
 * <p>응답 페이지의 마지막 요소 기준으로 다음 요청 시 필요한 커서 값을 전달합니다.</p>
 *
 * <ul>
 *   <li>{@code hasNext} → 다음 페이지가 존재하면 true</li>
 *   <li>{@code sortType}에 따라 커서 필드는 달라집니다:
 *     <ul>
 *       <li>{@code UPDATE_AT_DESC} → {@code dateCursor}, {@code id}</li>
 *       <li>{@code VIEW_COUNT_DESC}, {@code LIKE_COUNT_DESC} → {@code countCursor}, {@code id}</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Getter
@Builder
public class SliceNextCursor {

    private final Boolean hasNext;
    private final Long id;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;
    private final SortType sortType;
}
