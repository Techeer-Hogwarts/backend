package backend.techeerzip.domain.auth.repository;

import backend.techeerzip.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}