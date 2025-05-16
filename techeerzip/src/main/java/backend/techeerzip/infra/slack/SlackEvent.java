package backend.techeerzip.infra.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SlackEvent {
    @Getter
    @AllArgsConstructor
    public static class Channel<T> {
        private final T payload;
    }

    @Getter
    @AllArgsConstructor
    public static class DM<T> {
        private final T payload;
    }
}
