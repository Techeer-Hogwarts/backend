package backend.techeerzip.domain.bookmark.repository;

import java.util.List;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

public interface BookmarkRepositoryCustom {
    List<Bookmark> findBookmarksWithCursor(Long userId, String category, Long cursorId, int limit);
}
