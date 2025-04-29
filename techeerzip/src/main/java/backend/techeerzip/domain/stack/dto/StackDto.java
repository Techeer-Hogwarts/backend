package backend.techeerzip.domain.stack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StackDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String name;
        private String category;
        private String iconUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String category;
        private String iconUrl;
    }
}
