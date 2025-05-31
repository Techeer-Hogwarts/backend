package backend.techeerzip.domain.event.dto.response;

import backend.techeerzip.domain.event.entity.Event;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventListResponse {
    private final Long id;
    private final String category;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String url;

    public EventListResponse(Event event) {
        this.id = event.getId();
        this.category = event.getCategory();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.url = event.getUrl();
    }
}
