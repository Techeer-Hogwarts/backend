package backend.techeerzip.domain.techBloggingChallenge.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.DeleteAllRoundsRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundListResponse;
import backend.techeerzip.domain.techBloggingChallenge.service.TechBloggingChallengeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/tech-blogging")
@RequiredArgsConstructor
public class TechBloggingChallengeController implements TechBloggingChallengeSwagger {
    private final TechBloggingChallengeService challengeService;

    // 추후 관리자용 인증에 대해 고려할 예정

    @PostMapping("/rounds/batch") // 관리자용
    @Override
    public ResponseEntity<Void> createRounds(@Valid @RequestBody CreateRoundRequest request) {
        challengeService.createRounds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rounds") // 관리자용
    @Override
    public ResponseEntity<Void> createSingleRound(
            @Valid @RequestBody CreateSingleRoundRequest request) {
        challengeService.createSingleRound(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/rounds") // 관리자용
    @Override
    public ResponseEntity<Void> updateRound(@Valid @RequestBody UpdateRoundRequest request) {
        challengeService.updateRound(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rounds/{roundId}") // 관리자용
    @Override
    public ResponseEntity<Void> deleteRound(@PathVariable Long roundId) {
        challengeService.deleteRound(roundId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rounds") // 관리자용
    @Override
    public ResponseEntity<Void> deleteAllRounds(
            @Valid @RequestBody DeleteAllRoundsRequest request) {
        challengeService.deleteAllRounds(request.getYear(), request.isFirstHalf());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rounds") // 관리자용
    @Override
    public ResponseEntity<RoundListResponse> getRoundList() {
        RoundListResponse response = challengeService.getRoundList();
        return ResponseEntity.ok(response);
    }

    // 일반 유저용 API

    // 테크 블로깅 챌린지 지원 : 미래에 진행예정이나 현재 진행중인 챌린지에만 지원할 수 있음, 지원 취소는 없음

    // 테크 블로깅 목록 조회(필터 종류 조회) : 2025 상반기-회차 1~10, 2025 하반기-회차 1~10 조회 가능

    // 회차별 블로그 조회 (디폴트값 : 현재 진행중인 블로그 챌린지의 최신순 블로그 조회) : 회차별 블로그 조회 가능(상반기, 하반기 변경
    // 가능, 조회 옵션도 존재 : 최신순, 조회순, 이름순)

    // 챌린지에 참여한느 유저들의 출석 현황 조회
    // {
    // [
    // {
    // "userId": 1,
    // "userName": "김진희",
    // "sequence": [1, 0, 0, 4, 2, 1, 3, 2, 0, 0, 0, 0],
    // "totalCount": 13
    // },
    // {
    // "userId": 2,
    // "userName": "김진희",
    // "sequence": [1, 0, 0, 4, 2, 1, 3, 2, 0, 0, 0, 0]
    // "totalCount": 13
    // },
    // {
    // "userId": 3,
    // "userName": "김진희",
    // "sequence": [1, 0, 0, 4, 2, 1, 3, 2, 0, 0, 0, 0]
    // "totalCount": 13
    // },
    // ],
    // }
    // 위의 값이 예상 응답값으로, sequence는 회차의 제출한 블로그 갯수를 의미한다. 제출하지 않은 블로그는 0으로 표시된다. 또한 총
    // 제출 블로그 갯수도 조회 가능하다.
}
