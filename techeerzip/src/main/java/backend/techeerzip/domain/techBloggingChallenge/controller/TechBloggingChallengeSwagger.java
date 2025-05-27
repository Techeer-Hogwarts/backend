package backend.techeerzip.domain.techBloggingChallenge.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateTermRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tech Blogging Challenge", description = "기술 블로깅 챌린지 API")
public interface TechBloggingChallengeSwagger {

    @Operation(summary = "챌린지 기간 생성", description = "상반기/하반기 챌린지 기간과 회차를 생성합니다.")
    @PostMapping("/terms")
    ResponseEntity<TermDetailResponse> createTerm(@Valid @RequestBody CreateTermRequest request);

    @Operation(summary = "챌린지 기간 조회", description = "특정 챌린지 기간의 상세 정보를 조회합니다.")
    @GetMapping("/terms/{termId}")
    ResponseEntity<TermDetailResponse> getTerm(@PathVariable Long termId);

    @Operation(summary = "챌린지 기간 삭제", description = "특정 챌린지 기간과 관련 회차를 삭제합니다.")
    @DeleteMapping("/terms/{termId}")
    ResponseEntity<Void> deleteTerm(@PathVariable Long termId);

    @Operation(summary = "단일 회차 생성", description = "특정 챌린지 기간에 단일 회차를 생성합니다.")
    @PostMapping("/terms/rounds")
    ResponseEntity<RoundDetailResponse> createRound(
            @Valid @RequestBody CreateSingleRoundRequest request);

    @Operation(summary = "회차 수정", description = "특정 회차의 시작/종료 날짜를 수정합니다.")
    @PutMapping("/rounds")
    ResponseEntity<RoundDetailResponse> updateRound(@Valid @RequestBody UpdateRoundRequest request);

    @Operation(summary = "회차 삭제", description = "특정 회차를 삭제합니다.")
    @DeleteMapping("/rounds/{roundId}")
    ResponseEntity<Void> deleteRound(@PathVariable Long roundId);
}
