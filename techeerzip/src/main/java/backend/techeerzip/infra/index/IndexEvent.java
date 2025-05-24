package backend.techeerzip.infra.index;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class IndexEvent {

    @Getter
    @AllArgsConstructor
    public static class Create<T> {
        @NotNull
        private String index;
        @NotNull
        private T payload;
    }

    @Getter
    @AllArgsConstructor
    public static class Delete {
        @NotNull
        private String index;
        @NotNull
        private Long id;
    }
}