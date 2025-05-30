package backend.techeerzip.domain.projectTeam.repository;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {

    boolean existsByName(@NotNull String name);
}
