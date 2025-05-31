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
    @Query("UPDATE Bookmark b SET b.isDeleted = true WHERE b.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
