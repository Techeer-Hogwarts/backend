package backend.techeerzip.domain.resume.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.resume.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long>, ResumeRepositoryCustom {

    Optional<Resume> findByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("UPDATE Resume r SET r.isDeleted = true WHERE r.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Resume r SET r.isMain = false WHERE r.user.id = :userId AND r.isMain = true AND r.isDeleted = false")
    void unsetMainResumeByUserId(@Param("userId") Long userId);
}
