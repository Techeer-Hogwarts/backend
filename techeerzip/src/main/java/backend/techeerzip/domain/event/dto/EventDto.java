package backend.techeerzip.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {

        private String title;
        private String content;
        private String location;
        private String startDate;
        private String endDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String title;
        private String content;
        private String location;
        private String startDate;
        private String endDate;
    }
}
