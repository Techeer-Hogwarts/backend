package backend.techeerzip.domain.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.session.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {}
