package backend.techeerzip.domain.event.mapper;

import backend.techeerzip.domain.event.dto.request.EventIndexRequest;
import backend.techeerzip.domain.event.entity.Event;

public class EventMapper {
    private EventMapper() {
        throw new IllegalStateException("EventMapper is a utility class");
    }

    public static EventIndexRequest toIndexDto(Event event) {
        return new EventIndexRequest(
                event.getCategory(),
                String.valueOf(event.getId()),
                event.getTitle(),
                event.getUrl());
    }
}
