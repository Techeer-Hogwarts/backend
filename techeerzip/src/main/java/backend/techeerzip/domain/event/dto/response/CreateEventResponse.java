package backend.techeerzip.domain.event.dto.response;

import backend.techeerzip.domain.event.entity.Event;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateEventResponse {
    private final Long id;
    private final Long userId;
    private final String category;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String url;

    public CreateEventResponse(Event event) {
        this.id = event.getId();
        this.userId = event.getUser().getId();
        this.category = event.getCategory();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.url = event.getUrl();
    }
}
