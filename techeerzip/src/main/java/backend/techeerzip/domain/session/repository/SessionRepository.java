package backend.techeerzip.domain.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import backend.techeerzip.domain.session.entity.Session;

public interface SessionRepository
        extends JpaRepository<Session, Long>,
                JpaSpecificationExecutor<Session>,
                QuerydslPredicateExecutor<Session>,
                SessionDSLRepository {}
