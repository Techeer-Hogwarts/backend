package backend.techeerzip.domain.projectMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.global.entity.StatusCategory;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectTeamId(Long projectTeamId);

    List<ProjectMember> findByUserId(Long userId);

    List<ProjectMember> findAllByProjectTeamId(Long projectTeamId);

    boolean existsByUserIdAndProjectTeamIdAndDeletedFalseAndStatus(
            Long userId, Long projectTeamId, StatusCategory statusCategory);
}
