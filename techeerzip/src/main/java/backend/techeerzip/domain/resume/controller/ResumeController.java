package backend.techeerzip.domain.resume.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.resume.dto.request.ResumeCreateRequest;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeListResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeResponse;
import backend.techeerzip.domain.resume.service.ResumeService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import lombok.RequiredArgsConstructor;

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
            @RequestPart ResumeCreateRequest request) {
        ResumeCreateResponse response =
                resumeService.createResume(
                        userId,
                        file,
                        request.getTitle(),
                        request.getPosition(),
                        request.getCategory(),
                        request.getIsMain());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getResumeById(@PathVariable Long resumeId) {
        ResumeResponse response = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId, @UserId Long userId) {
        resumeService.deleteResumeById(resumeId, userId);
        return ResponseEntity.noContent().build();
    }

    // 메인 이력서 업데이트
    @PatchMapping("/{resumeId}")
    public ResponseEntity<Void> updateMainResume(@PathVariable Long resumeId, @UserId Long userId) {
        resumeService.updateMainResume(resumeId, userId);
        return ResponseEntity.ok().build();
    }

    // 이력서 목록 조회
    @GetMapping()
    public ResponseEntity<ResumeListResponse> getResumes(
            @RequestParam(required = false) List<String> position,
            @RequestParam(required = false) List<Integer> year,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        ResumeListResponse resumes =
                resumeService.getResumes(position, year, category, cursorId, limit);

        return ResponseEntity.ok(resumes);
    }

    // 특정 유저의 이력서 목록 조회 (커서 기반 페이지네이션)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResumeListResponse> getUserResumes(
            @PathVariable Long userId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer limit) {
        ResumeListResponse userResumes = resumeService.getUserResumes(userId, cursorId, limit);
        return ResponseEntity.ok(userResumes);
    }

    // 인기 이력서 목록 조회 (커서 기반 페이지네이션)
    @GetMapping("/best")
    public ResponseEntity<ResumeListResponse> getBestResumes(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer limit) {
        ResumeListResponse bestResumes = resumeService.getBestResumes(cursorId, limit);
        return ResponseEntity.ok(bestResumes);
    }
}
