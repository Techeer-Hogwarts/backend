package backend.techeerzip.domain.projectTeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;

public interface ProjectResultImageRepository extends JpaRepository<ProjectResultImage, Long> {

    int countByProjectTeamId(Long projectTeamId);
}
