package backend.techeerzip.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.event.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    Optional<Event> findByIdAndIsDeletedFalse(Long id);
}
