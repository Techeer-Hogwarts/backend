package backend.techeerzip.domain.bookmark.repository;

import backend.techeerzip.domain.bookmark.entity.Bookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Bookmark b SET b.isDeleted = true WHERE b.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
