package backend.techeerzip.domain.projectMember.repository;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectTeamId(Long projectTeamId);
    List<ProjectMember> findByUserId(Long userId);
} 