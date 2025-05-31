package backend.techeerzip.domain.event.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class EventListResponse {
    @Schema(description = "이벤트 목록")
    private final List<EventResponse> data;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;

    public EventListResponse(List<EventResponse> events, int limit) {
        this.hasNext = events.size() > limit;
        this.data = hasNext ? events.subList(0, limit) : events;
        this.nextCursor = hasNext ? events.get(limit - 1).getId() : null;
    }
}
