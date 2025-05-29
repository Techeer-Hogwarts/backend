package backend.techeerzip.domain.resume.repository;

import backend.techeerzip.domain.resume.entity.Resume;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);

    Optional<Resume> findByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("DELETE Resume r WHERE r.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
