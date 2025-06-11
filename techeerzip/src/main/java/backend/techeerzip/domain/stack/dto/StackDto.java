package backend.techeerzip.domain.stack.dto;

import backend.techeerzip.domain.stack.entity.StackCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StackDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String name;
        private String category;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private StackCategory category;
    }
}
