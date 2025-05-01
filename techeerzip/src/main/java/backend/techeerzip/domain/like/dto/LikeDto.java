package backend.techeerzip.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LikeDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long targetId;
        private String targetType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private Long targetId;
        private String targetType;
    }
}
