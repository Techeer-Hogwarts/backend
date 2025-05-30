package backend.techeerzip.domain.event.dto.request;

import backend.techeerzip.domain.event.entity.Event;

public record EventIndexRequest(
    String category,
    String id,
    String title,
    String url
) {
    public EventIndexRequest(Event event) {
        this(
                event.getCategory(),
                String.valueOf(event.getId()),
                event.getTitle(),
                event.getUrl()
        );
    }
}
