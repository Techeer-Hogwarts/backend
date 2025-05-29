package backend.techeerzip.domain.like.repository;

import backend.techeerzip.domain.like.entity.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    Optional<Like> findByUserIdAndContentIdAndCategory(
            Long userId, Long contentId, String category);

    @Modifying
    @Query("DELETE Like l WHERE l.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
