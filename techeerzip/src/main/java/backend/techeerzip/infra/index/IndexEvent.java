package backend.techeerzip.infra.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class IndexEvent {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Create<T> {
        private final String index;
        private final T payload;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Delete {
        private final String index;
        private final Long id;
    }
}
