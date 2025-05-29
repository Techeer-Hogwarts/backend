package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface SessionRepository
        extends JpaRepository<Session, Long>,
                JpaSpecificationExecutor<Session>,
                QuerydslPredicateExecutor<Session> {
    Optional<Session> findByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("DELETE Session s WHERE s.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
