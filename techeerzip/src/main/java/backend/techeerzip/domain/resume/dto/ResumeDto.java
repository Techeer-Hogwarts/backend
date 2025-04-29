package backend.techeerzip.domain.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ResumeDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String title;
        private String content;
        private String githubUrl;
        private String blogUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String title;
        private String content;
        private String githubUrl;
        private String blogUrl;
    }
}
