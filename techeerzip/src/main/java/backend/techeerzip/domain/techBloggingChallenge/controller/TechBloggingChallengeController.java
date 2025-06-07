package backend.techeerzip.domain.techBloggingChallenge.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.ApplyChallengeRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.BlogChallengeCursorRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateTermRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.AttendanceStatusResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.BlogChallengeListResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermRoundsSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.service.TechBloggingChallengeService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/tech-blogging")
@RequiredArgsConstructor
public class TechBloggingChallengeController implements TechBloggingChallengeSwagger {
    private final TechBloggingChallengeService challengeService;
    private final CustomLogger logger;

    // 특정 분기(연도/반기) 챌린지 지원 (일반 유저용 API)
    @PostMapping("/apply")
    public ResponseEntity<Void> applyChallenge(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody ApplyChallengeRequest request) {
        logger.info("userId: {}", userId);
        challengeService.applyChallenge(userId, request);
        return ResponseEntity.ok().build();
    }

    // 추후 관리자용 인증에 대해 고려할 예정
    // 챌린지 기간 생성 (회차 포함) (관리자용 API)
    @PostMapping("/terms")
    @Override
    public ResponseEntity<TermDetailResponse> createTerm(
            @Valid @RequestBody CreateTermRequest request) {
        TermDetailResponse response = challengeService.createTerm(request);
        return ResponseEntity.ok(response);
    }

    // 챌린지 기간 조회
    @GetMapping("/terms/{termId}")
    @Override
    public ResponseEntity<TermDetailResponse> getTerm(@PathVariable Long termId) {
        TermDetailResponse response = challengeService.getTerm(termId);
        return ResponseEntity.ok(response);
    }

    // 추후 관리자용 인증에 대해 고려할 예정
    // 챌린지 기간 삭제 (관리자용 API)
    @DeleteMapping("/terms/{termId}")
    @Override
    public ResponseEntity<Void> deleteTerm(@PathVariable Long termId) {
        challengeService.deleteTerm(termId);
        return ResponseEntity.noContent().build();
    }

    // 추후 관리자용 인증에 대해 고려할 예정
    // 단일 회차 생성 (관리자용 API)
    @PostMapping("/terms/rounds")
    @Override
    public ResponseEntity<RoundDetailResponse> createRound(
            @Valid @RequestBody CreateSingleRoundRequest request) {
        RoundDetailResponse response = challengeService.createRound(request);
        return ResponseEntity.ok(response);
    }

    // 추후 관리자용 인증에 대해 고려할 예정
    // 회차 수정 (관리자용 API)
    @PutMapping("/rounds")
    @Override
    public ResponseEntity<RoundDetailResponse> updateRound(
            @Valid @RequestBody UpdateRoundRequest request) {
        RoundDetailResponse response = challengeService.updateRound(request);
        return ResponseEntity.ok(response);
    }

    // 추후 관리자용 인증에 대해 고려할 예정
    // 회차 삭제 (관리자용 API)
    @DeleteMapping("/rounds/{roundId}")
    @Override
    public ResponseEntity<Void> deleteRound(@PathVariable Long roundId) {
        challengeService.deleteRound(roundId);
        return ResponseEntity.noContent().build();
    }

    // 모든 회차 조회 (필터 종류 조회에도 쓰임)
    @GetMapping("/terms")
    public ResponseEntity<List<TermSummaryResponse>> getTermList() {
        List<TermSummaryResponse> response = challengeService.getTermList();
        return ResponseEntity.ok(response);
    }

    // 챌린지 기간 및 회차 요약 조회
    @GetMapping("/terms/{termId}/summary")
    public ResponseEntity<TermRoundsSummaryResponse> getTermRoundsSummary(
            @PathVariable Long termId) {
        TermRoundsSummaryResponse response = challengeService.getTermRoundsSummary(termId);
        return ResponseEntity.ok(response);
    }

    // 챌린지 출석 현황 조회 (termId 없으면 현재 진행중인 챌린지)
    @GetMapping("/terms/attendance")
    public ResponseEntity<List<AttendanceStatusResponse>> getAttendanceStatus(
            @RequestParam(value = "termId", required = false) Long termId) {
        List<AttendanceStatusResponse> response = challengeService.getAttendanceStatus(termId);
        return ResponseEntity.ok(response);
    }

    // 회차별 블로그 커서 기반 조회
    @GetMapping("/rounds/blogs")
    public ResponseEntity<BlogChallengeListResponse> getBlogsByRoundCursor(
            @ModelAttribute BlogChallengeCursorRequest request) {
        BlogChallengeListResponse response = challengeService.getBlogsByRoundCursor(request);
        return ResponseEntity.ok(response);
    }
}
