package backend.techeerzip.domain.event.dto.request;

import backend.techeerzip.domain.event.entity.Event;
import lombok.Getter;

@Getter
public class IndexEventRequest {

    private final String category;
    private final String id;
    private final String title;
    private final String url;

    public IndexEventRequest(Event event) {
        this.category = event.getCategory();
        this.id = event.getId().toString();
        this.title = event.getTitle();
        this.url = event.getUrl();
    }
}
