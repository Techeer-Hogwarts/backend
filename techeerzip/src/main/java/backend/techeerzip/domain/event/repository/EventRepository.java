package backend.techeerzip.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.event.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {
    Optional<Event> findByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("UPDATE Event e SET e.isDeleted = true WHERE e.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
