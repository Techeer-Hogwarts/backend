package backend.techeerzip.domain.projectMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project-team/{projectTeamId}")
    public ResponseEntity<?> getProjectMembersByProjectTeamId(@PathVariable Long projectTeamId) {
        // TODO: Implement getting project members by project team ID
        return ResponseEntity.ok().build();
    }
}
