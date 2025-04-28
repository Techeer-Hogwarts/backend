package backend.techeerzip.domain.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkDto {
    @Getter
    @NoArgsConstructor
    public static class Create {
        private String title;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String url;
        private Long userId;
    }
} 