package backend.techeerzip.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    Optional<Like> findByUserIdAndContentIdAndCategory(
            Long userId, Long contentId, String category);
}
