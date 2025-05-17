package backend.techeerzip.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class EventDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {

        private Long id;
        private Long userId;
        private String category;
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private Long userId;
        private String category;
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String url;
        private UserDto user;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserDto {
            private String name;
            private String nickname;
            private String profileImage;
        }
    }
}
