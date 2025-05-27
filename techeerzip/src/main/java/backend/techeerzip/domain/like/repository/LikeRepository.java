package backend.techeerzip.domain.like.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l " +
           "WHERE l.user.id = :userId " +
           "AND l.category = :category " +
           "AND l.isDeleted = false " +
           "AND l.contentId < :cursorId " +
           "ORDER BY l.contentId DESC")
    List<Like> findActiveLikesByUserIdAndCategoryWithCursor(
        @Param("userId") Long userId,
        @Param("category") String category,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );
}
