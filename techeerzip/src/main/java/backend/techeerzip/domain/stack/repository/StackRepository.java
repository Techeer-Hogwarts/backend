package backend.techeerzip.domain.stack.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.stack.entity.Stack;

public interface StackRepository extends JpaRepository<Stack, Long> {

    List<Stack> findAllByIsDeletedFalse();

    List<Stack> findAllByNameIn(Collection<String> names);
}
