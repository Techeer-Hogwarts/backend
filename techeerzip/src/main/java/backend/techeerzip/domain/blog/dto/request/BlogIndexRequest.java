package backend.techeerzip.domain.blog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BlogIndexRequest {
    private final String id;
    private final String title;
    private final String url;
    private final String thumbnail;
    private final String date;
    private final String userId;
    private final String userName;
    private final String userProfileImage;
    private final String[] stack;
}
