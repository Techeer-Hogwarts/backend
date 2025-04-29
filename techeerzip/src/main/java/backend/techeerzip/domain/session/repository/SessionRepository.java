package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
} 