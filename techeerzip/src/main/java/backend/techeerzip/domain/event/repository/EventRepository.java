package backend.techeerzip.domain.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByStartDateDesc();

    @Modifying
    @Query("DELETE Event e WHERE e.user.id = :userId")
    void deletedByUserId(@Param("userId") Long userId);
}
