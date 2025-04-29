package backend.techeerzip.domain.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SessionDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long userId;
        private String ipAddress;
        private String userAgent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String sessionToken;
        private String ipAddress;
        private String userAgent;
        private String expiresAt;
    }
}
