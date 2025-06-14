package backend.techeerzip.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository
        extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    Optional<Bookmark> findByUserIdAndContentIdAndCategory(
            Long userId, Long contentId, String category);

    @Modifying
    @Query("UPDATE Bookmark b SET b.isDeleted = true WHERE b.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
