package backend.techeerzip.domain.resume.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.resume.dto.ResumeDto;
import backend.techeerzip.domain.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeDto.Response> createResume(@RequestBody ResumeDto.Create request) {
        // TODO: Implement resume creation
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getResumesByUserId(@PathVariable Long userId) {
        // TODO: Implement getting resumes by user ID
        return ResponseEntity.ok().build();
    }
}
