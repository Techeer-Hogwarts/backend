package backend.techeerzip.domain.projectTeam.repository;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {

    Optional<ProjectTeam> findByName(String name);

    boolean existsByName(@NotNull String name);
}
