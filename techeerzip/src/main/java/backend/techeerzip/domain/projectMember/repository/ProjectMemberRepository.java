package backend.techeerzip.domain.projectMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectTeamId(Long projectTeamId);

    List<ProjectMember> findByUserId(Long userId);

    @Modifying
    @Query("DELETE ProjectMember p WHERE p.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
