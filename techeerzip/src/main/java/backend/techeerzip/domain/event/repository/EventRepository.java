package backend.techeerzip.domain.event.repository;

import backend.techeerzip.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsDeletedFalse();
    List<Event> findByIsDeletedFalseAndCategoryIn(List<String> categories);
    List<Event> findByIsDeletedFalseAndTitleContainingIgnoreCase(String keyword);
    List<Event> findByIsDeletedFalseAndTitleContainingIgnoreCaseAndCategoryIn(String keyword, List<String> categories);

    @Query("SELECT e FROM Event e WHERE e.isDeleted = false " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categories IS NULL OR e.category IN :categories)")
    Page<Event> findEvents(String keyword, List<String> categories, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.isDeleted = false " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categories IS NULL OR e.category IN :categories)")
    long countEvents(String keyword, List<String> categories);
}
