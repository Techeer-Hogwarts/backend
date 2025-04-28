package backend.techeerzip.domain.projectTeam.repository;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {
    List<ProjectTeam> findAllByOrderByStartDateDesc();
} 