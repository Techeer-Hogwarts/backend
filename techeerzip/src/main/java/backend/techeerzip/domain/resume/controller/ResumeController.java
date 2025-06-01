package backend.techeerzip.domain.resume.controller;

import backend.techeerzip.domain.resume.dto.request.ResumeCreateRequest;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeResponse;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.resume.dto.ResumeDto;
import backend.techeerzip.domain.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v3/resumes")
@RequiredArgsConstructor
public class ResumeController implements ResumeSwagger {
    private final ResumeService resumeService;
    private final CustomLogger logger;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeCreateResponse> createResume(
            @UserId Long userId,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart ResumeCreateRequest request
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

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getResumesByUserId(
            @PathVariable Long resumeId
    ) {
        ResumeResponse response = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable Long resumeId,
            @UserId Long userId
    ) {
        resumeService.deleteResumeById(resumeId, userId);
        return ResponseEntity.noContent().build();
    }

    // 메인 이력서 업데이트
    @PatchMapping("/{resumeId}")
    public ResponseEntity<Void> updateMainResume(
            @PathVariable Long resumeId,
            @UserId Long userId
    ) {
        resumeService.updateMainResume(resumeId, userId);
        return ResponseEntity.ok().build();
    }
}
