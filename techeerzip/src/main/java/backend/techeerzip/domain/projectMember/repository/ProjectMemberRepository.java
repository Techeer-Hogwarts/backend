package backend.techeerzip.domain.projectMember.repository;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.global.entity.StatusCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectTeamId(Long projectTeamId);

    List<ProjectMember> findAllByProjectTeamId(Long projectTeamId);

    boolean existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
            Long userId, Long projectTeamId, StatusCategory statusCategory);

    Optional<ProjectMember> findByProjectTeamIdAndUserId(Long teamId, Long userId);

    Optional<ProjectMember> findByProjectTeamIdAndUserIdAndStatus(
            Long teamId, Long applicantId, StatusCategory statusCategory);

    @Modifying
    @Query("UPDATE ProjectMember p SET p.isDeleted = true WHERE p.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
