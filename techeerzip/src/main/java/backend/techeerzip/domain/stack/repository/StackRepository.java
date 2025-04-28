package backend.techeerzip.domain.stack.repository;

import backend.techeerzip.domain.stack.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StackRepository extends JpaRepository<Stack, Long> {
    List<Stack> findByCategory(String category);
} 