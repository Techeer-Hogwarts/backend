package backend.techeerzip.domain.projectTeam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.projectTeam.dto.ProjectTeamDto;
import backend.techeerzip.domain.projectTeam.service.ProjectTeamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project-teams")
@RequiredArgsConstructor
public class ProjectTeamController {
    private final ProjectTeamService projectTeamService;

    @PostMapping
    public ResponseEntity<ProjectTeamDto.Response> createProjectTeam(
            @RequestBody ProjectTeamDto.Create request) {
        // TODO: Implement project team creation
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllProjectTeams() {
        // TODO: Implement getting all project teams
        return ResponseEntity.ok().build();
    }
}
