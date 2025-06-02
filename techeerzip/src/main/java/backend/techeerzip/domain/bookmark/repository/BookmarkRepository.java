package backend.techeerzip.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository
        extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    Optional<Bookmark> findByUserIdAndContentIdAndCategory(
            Long userId, Long contentId, String category);
}
