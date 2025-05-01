package backend.techeerzip.domain.bookmark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
}
