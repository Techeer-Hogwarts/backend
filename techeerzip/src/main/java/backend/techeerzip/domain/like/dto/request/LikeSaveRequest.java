package backend.techeerzip.domain.like.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeSaveRequest {
    private Long contentId;
    private String category;
    private Boolean likeStatus;

    public LikeSaveRequest(Long contentId, String category, Boolean likeStatus) {
        this.contentId = contentId;
        this.category = category;
        this.likeStatus = likeStatus;
    }
}
