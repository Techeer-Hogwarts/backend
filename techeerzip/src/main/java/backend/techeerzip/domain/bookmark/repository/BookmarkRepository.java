package backend.techeerzip.domain.bookmark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);

    @Modifying
    @Query("DELETE Bookmark b WHERE b.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
