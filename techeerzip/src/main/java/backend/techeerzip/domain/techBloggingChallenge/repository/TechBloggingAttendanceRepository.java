package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingAttendance;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.user.entity.User;

@Repository
public interface TechBloggingAttendanceRepository
        extends JpaRepository<TechBloggingAttendance, Long>,
                TechBloggingAttendanceRepositoryCustom {
    boolean existsByUserAndTechBloggingRoundAndBlog(User user, TechBloggingRound round, Blog blog);

    int countByUserAndTechBloggingRoundAndIsDeletedFalse(User user, TechBloggingRound round);

    List<TechBloggingAttendance> findByUserAndTechBloggingRoundAndIsDeletedFalse(
            User user, TechBloggingRound round);
}
