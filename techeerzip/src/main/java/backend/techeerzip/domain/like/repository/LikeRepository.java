package backend.techeerzip.domain.like.repository;

import backend.techeerzip.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 필요한 쿼리 메서드 추가
} 