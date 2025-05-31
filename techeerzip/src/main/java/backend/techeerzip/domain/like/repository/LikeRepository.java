package backend.techeerzip.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    Optional<Like> findByUserIdAndContentIdAndCategory(
            Long userId, Long contentId, String category);

    @Modifying
    @Query("UPDATE Like l SET l.isDeleted = true WHERE l.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
