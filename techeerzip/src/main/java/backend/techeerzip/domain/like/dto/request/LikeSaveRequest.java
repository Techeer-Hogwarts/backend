package backend.techeerzip.domain.like.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeSaveRequest {
    private Long contentId;
    private String category;
    private String type;

    public LikeSaveRequest(Long contentId, String category, String type) {
        this.contentId = contentId;
        this.category = category;
        this.type = type;
    }
}
