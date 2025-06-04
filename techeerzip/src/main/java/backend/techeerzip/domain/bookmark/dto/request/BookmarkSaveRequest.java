package backend.techeerzip.domain.bookmark.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkSaveRequest {
    private Long contentId;
    private String category;
    private Boolean bookmarkStatus;

    public BookmarkSaveRequest(Long contentId, String category, Boolean bookmarkStatus) {
        this.contentId = contentId;
        this.category = category;
        this.bookmarkStatus = bookmarkStatus;
    }
}
