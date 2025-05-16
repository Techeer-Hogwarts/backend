package backend.techeerzip.infra.index;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class IndexEvent {

    @Getter
    @AllArgsConstructor
    public static class Create<T> {
        private String index;
        private T payload;
    }

    @Getter
    @AllArgsConstructor
    public static class Delete {
        private String index;
        private Long id;
    }
}
