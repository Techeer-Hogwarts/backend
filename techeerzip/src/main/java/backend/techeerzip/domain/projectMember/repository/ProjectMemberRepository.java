package backend.techeerzip.domain.projectMember.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.global.entity.StatusCategory;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectTeamId(Long projectTeamId);

    List<ProjectMember> findAllByProjectTeamId(Long projectTeamId);

    boolean existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
            Long userId, Long projectTeamId, StatusCategory statusCategory);

    boolean existsByProjectTeamIdAndUserId(Long teamId, Long userId);

    Optional<ProjectMember> findByProjectTeamIdAndUserId(Long teamId, Long userId);

    Optional<ProjectMember> findByProjectTeamIdAndUserIdAndStatus(
            Long teamId, Long applicantId, StatusCategory statusCategory);
}
