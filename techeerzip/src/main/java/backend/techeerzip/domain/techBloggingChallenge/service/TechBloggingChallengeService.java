package backend.techeerzip.domain.techBloggingChallenge.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundListResponse;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundAlreadyExistsException;
import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundInfiniteLoopException;
import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundNotFoundException;
import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundPastDateException;
import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundPeriodTooShortException;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingRoundRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechBloggingChallengeService {
    private static final int MINIMUM_ROUND_DURATION_DAYS = 13; // 최소 2주(14일) 미만 체크
    private final TechBloggingRoundRepository roundRepository;

    private LocalDate[] calculatePeriodDates(int year, boolean firstHalf) {
        LocalDate start = firstHalf ? LocalDate.of(year, 3, 1) : LocalDate.of(year, 9, 1);
        LocalDate end = firstHalf
                ? LocalDate.of(year, 7, 31)
                : LocalDate.of(year + 1, 2, YearMonth.of(year + 1, 2).lengthOfMonth());
        return new LocalDate[] { start, end };
    }

    // 챌린지 회차 생성 (2주 단위 자동 분할) 동일한 회차 생성 불가(예외처리 필요)
    @Transactional
    public void createRounds(CreateRoundRequest request) {
        int year = request.getYear();
        boolean firstHalf = request.isFirstHalf();
        int interval = request.getIntervalWeeks();

        LocalDate[] periodDates = calculatePeriodDates(year, firstHalf);
        LocalDate start = periodDates[0];
        LocalDate end = periodDates[1];

        validateNotPastDate(start);
        validateNoDuplicateRound(firstHalf, start, end); // 동일 기간에 중복된 회차가 있는지 체크
        generateRounds(start, end, interval, firstHalf);
    }

    private void generateRounds(LocalDate start, LocalDate end, int interval, boolean firstHalf) {
        int sequence = 1;
        int maxIterations = 100; // 무한루프 방지 안전장치
        int iterations = 0;
        int baseYear = start.getYear(); // 1회차 연도 저장
        while (!start.isAfter(end)) {
            if (++iterations > maxIterations) {
                throw new TechBloggingRoundInfiniteLoopException();
            }
            start = moveToNextWeekday(start);
            LocalDate roundEnd = start.plusWeeks(interval).minusDays(1);
            boolean isLastRound = false;
            // 마지막 회차 확인
            if (roundEnd.isAfter(end)) {
                isLastRound = true;
            }
            TechBloggingRound round = TechBloggingRound.create(start, roundEnd, sequence++, firstHalf, baseYear);
            roundRepository.save(round);
            if (isLastRound)
                break;
            start = roundEnd.plusDays(1);
        }
    }

    private LocalDate moveToNextWeekday(LocalDate date) {
        while (date.getDayOfWeek().getValue() >= 6) { // 6: 토, 7: 일
            date = date.plusDays(1);
        }
        return date;
    }

    private void validateNotPastDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new TechBloggingRoundPastDateException();
        }
    }

    private void validateNoDuplicateRound(boolean firstHalf, LocalDate start, LocalDate end) {
        boolean exists = roundRepository
                .existsByFirstHalfAndIsDeletedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        firstHalf, end, start);
        if (exists) {
            throw new TechBloggingRoundAlreadyExistsException();
        }
    }

    // 챌린지 회차 수정
    @Transactional
    public void updateRound(UpdateRoundRequest request) {
        TechBloggingRound round = roundRepository
                .findById(request.getRoundId())
                .orElseThrow(() -> new TechBloggingRoundNotFoundException());
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate().plusDays(1));
        if (days < MINIMUM_ROUND_DURATION_DAYS) { // 14일 미만
            throw new TechBloggingRoundPeriodTooShortException();
        }
        round.updateDates(request.getStartDate(), request.getEndDate());
    }

    // 챌린지 회차 삭제 (soft delete)
    @Transactional
    public void deleteRound(Long roundId) {
        TechBloggingRound round = roundRepository
                .findById(roundId)
                .orElseThrow(() -> new TechBloggingRoundNotFoundException());
        round.softDelete();
    }

    // 챌린지 회차 상세 조회
    @Transactional(readOnly = true)
    public RoundDetailResponse getRoundDetail(Long roundId) {
        TechBloggingRound round = roundRepository
                .findById(roundId)
                .orElseThrow(() -> new TechBloggingRoundNotFoundException());

        String year = String.valueOf(round.getBaseYear());
        String half = round.isFirstHalf() ? "상반기" : "하반기";
        String roundName = String.format("%s %s %d회차", year, half, round.getSequence());

        return new RoundDetailResponse(
                round.getId(),
                roundName,
                round.getSequence(),
                round.getStartDate(),
                round.getEndDate(),
                round.isFirstHalf());
    }

    // 5. 챌린지 회차 목록 조회
    @Transactional(readOnly = true)
    public RoundListResponse getRoundList() {
        List<RoundDetailResponse> roundDetails = roundRepository.findByIsDeletedFalse().stream()
                .map(this::convertToRoundDetailResponse)
                .collect(Collectors.toList());

        return new RoundListResponse(roundDetails);
    }

    private RoundDetailResponse convertToRoundDetailResponse(TechBloggingRound round) {
        String year = String.valueOf(round.getBaseYear());
        String half = round.isFirstHalf() ? "상반기" : "하반기";
        String roundName = String.format("%s %s %d회차", year, half, round.getSequence());

        return new RoundDetailResponse(
                round.getId(),
                roundName,
                round.getSequence(),
                round.getStartDate(),
                round.getEndDate(),
                round.isFirstHalf());
    }

    // 전체 상반기/하반기 회차 삭제
    @Transactional
    public void deleteAllRounds(int year, boolean firstHalf) {
        LocalDate[] periodDates = calculatePeriodDates(year, firstHalf);
        LocalDate start = periodDates[0];
        LocalDate end = periodDates[1];

        List<TechBloggingRound> rounds = roundRepository.findByFirstHalfAndStartDateBetween(firstHalf, start, end);
        rounds.forEach(TechBloggingRound::softDelete);
    }

    // 4. 챌린지 지원
    @Transactional
    public void applyChallenge(Long userId, Long roundId) {
        // TODO: 유저가 챌린지 지원
    }

    // 6. 회차별 블로그 조회
    @Transactional(readOnly = true)
    public void getBlogsByRound(Long roundId, String sortOption) {
        // TODO: 회차별 블로그 목록 조회 (최신순, 조회순, 이름순)
    }

    // 7. 출석 현황 조회
    @Transactional(readOnly = true)
    public void getAttendanceStatus(Long roundId) {
        // TODO: 회차별 유저 출석 현황
    }

    // 8. 스케줄러: 매주 새벽 4시 출석 체크
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void checkAttendance() {
        // TODO: 각 회차별, 유저별로 2주 내 블로그 제출 여부 자동 체크
    }

    // 단일 회차 생성
    @Transactional
    public void createSingleRound(CreateSingleRoundRequest request) {
        // 과거 날짜 체크
        validateNotPastDate(request.getStartDate());

        // 동일 기간에 중복된 회차가 있는지 체크
        validateNoDuplicateRound(
                request.isFirstHalf(), request.getStartDate(), request.getEndDate());

        // 회차 생성
        int baseYear = request.getStartDate().getYear();
        TechBloggingRound round = TechBloggingRound.create(
                request.getStartDate(),
                request.getEndDate(),
                request.getSequence(),
                request.isFirstHalf(),
                baseYear);
        roundRepository.save(round);
    }
}
