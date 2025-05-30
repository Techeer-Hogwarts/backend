package backend.techeerzip.domain.projectMember.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.global.entity.StatusCategory;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectTeamId(Long projectTeamId);

    List<ProjectMember> findAllByProjectTeamId(Long projectTeamId);

    boolean existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
            Long userId, Long projectTeamId, StatusCategory statusCategory);

    Optional<ProjectMember> findByProjectTeamIdAndUserId(Long teamId, Long userId);

    Optional<ProjectMember> findByProjectTeamIdAndUserIdAndStatus(
            Long teamId, Long applicantId, StatusCategory statusCategory);

    @Modifying
    @Query("DELETE ProjectMember p WHERE p.user.id = :userId")
    void deletedByUserId(@Param("userId") Long userId);
}
