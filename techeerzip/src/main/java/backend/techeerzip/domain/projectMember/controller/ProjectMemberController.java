package backend.techeerzip.domain.projectMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberDto;
import backend.techeerzip.domain.projectMember.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/project-members")
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectMemberDto.Response> createProjectMember(
            @RequestBody ProjectMemberDto.Create request) {
        // TODO: Implement project member creation
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project-team/{projectTeamId}")
    public ResponseEntity<?> getProjectMembersByProjectTeamId(@PathVariable Long projectTeamId) {
        // TODO: Implement getting project members by project team ID
        return ResponseEntity.ok().build();
    }
}
