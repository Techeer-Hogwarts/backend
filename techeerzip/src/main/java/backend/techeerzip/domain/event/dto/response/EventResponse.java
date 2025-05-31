package backend.techeerzip.domain.event.dto.response;

import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class EventResponse {
    private final Long id;
    private final Long userId;
    private final String category;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String url;
    private final UserInfo user;

    public EventResponse(Event event) {
        User eventUser = event.getUser();

        this.id = event.getId();
        this.userId = eventUser.getId();
        this.category = event.getCategory();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.url = event.getUrl();
        this.user = new UserInfo(
                eventUser.getName(),
                eventUser.getNickname(),
                eventUser.getProfileImage()
        );
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String name;
        private String nickname;
        private String profileImage;
    }
}
