package backend.techeerzip.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.user.entity.PermissionRequest;
import backend.techeerzip.global.entity.StatusCategory;

public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, Long> {
    List<PermissionRequest> findByStatus(StatusCategory status);

    @Modifying
    @Query(
            "UPDATE PermissionRequest p SET p.status = :status WHERE p.user.id = :userId AND p.status = backend.techeerzip.global.entity.StatusCategory.PENDING")
    int updateStatusByUserId(@Param("userId") Long userId, @Param("status") StatusCategory status);
}
