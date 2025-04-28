package backend.techeerzip.domain.studyMember.controller;

import backend.techeerzip.domain.studyMember.dto.StudyMemberDto;
import backend.techeerzip.domain.studyMember.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-members")
@RequiredArgsConstructor
public class StudyMemberController {
    private final StudyMemberService studyMemberService;

    @PostMapping
    public ResponseEntity<StudyMemberDto.Response> createStudyMember(@RequestBody StudyMemberDto.Create request) {
        // TODO: Implement study member creation
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study-team/{studyTeamId}")
    public ResponseEntity<?> getStudyMembersByStudyTeamId(@PathVariable Long studyTeamId) {
        // TODO: Implement getting study members by study team ID
        return ResponseEntity.ok().build();
    }
} 