package backend.techeerzip.domain.projectTeam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;

public interface ProjectTeamStackRepository extends JpaRepository<TeamStack, Long> {

    List<TeamStack> findAllByProjectTeamId(Long id);

    void deleteAllByProjectTeam(ProjectTeam projectTeam);
}
