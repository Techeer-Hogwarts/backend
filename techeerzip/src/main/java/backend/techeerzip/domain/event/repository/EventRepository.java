package backend.techeerzip.domain.event.repository;

import backend.techeerzip.domain.event.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.isDeleted = false " +
            "AND (:keyword IS NULL OR e.title LIKE %:keyword%) " +
            "AND (:category IS NULL OR e.category IN :category)")
    List<Event> findByFilters(@Param("keyword") String keyword,
                              @Param("category") List<String> category,
                              Pageable pageable);

    java.util.Optional<Event> findByIdAndIsDeletedFalse(Long id);
}

