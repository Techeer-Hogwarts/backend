package backend.techeerzip.domain.event.repository;

import backend.techeerzip.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    Optional<Event> findByIdAndIsDeletedFalse(Long id);
}
