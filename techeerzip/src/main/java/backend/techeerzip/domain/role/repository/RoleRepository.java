package backend.techeerzip.domain.role.repository;

import backend.techeerzip.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
    
    long count();
} 