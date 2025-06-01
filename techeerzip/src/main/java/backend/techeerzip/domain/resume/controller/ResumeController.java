package backend.techeerzip.domain.resume.controller;

import backend.techeerzip.domain.resume.dto.request.ResumeCreateRequest;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.resume.dto.ResumeDto;
import backend.techeerzip.domain.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v3/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    private final CustomLogger logger;

    @PostMapping
    public ResponseEntity<ResumeCreateResponse> createResume(
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart ResumeCreateRequest request,
            @UserId Long userId
    ) {
        ResumeCreateResponse response = resumeService.createResume(
                userId,
                file,
                request.getTitle(),
                request.getPosition(),
                request.getCategory(),
                request.getIsMain()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getResumesByUserId(@PathVariable Long userId) {
        // TODO: Implement getting resumes by user ID
        return ResponseEntity.ok().build();
    }
}
