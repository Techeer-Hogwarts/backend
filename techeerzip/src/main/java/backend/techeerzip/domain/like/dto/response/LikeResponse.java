package backend.techeerzip.domain.like.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class LikeResponse {
    private Long likeId;
    private Long blogId;
    private Long userId;
    private LocalDateTime createdAt;
}
