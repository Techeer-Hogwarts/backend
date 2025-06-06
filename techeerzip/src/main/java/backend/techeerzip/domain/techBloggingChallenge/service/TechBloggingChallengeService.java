package backend.techeerzip.domain.techBloggingChallenge.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.*;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.*;
import backend.techeerzip.domain.techBloggingChallenge.entity.DateRange;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingAttendance;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTermParticipant;
import backend.techeerzip.domain.techBloggingChallenge.entity.TermPeriod;
import backend.techeerzip.domain.techBloggingChallenge.exception.*;
import backend.techeerzip.domain.techBloggingChallenge.mapper.TechBloggingChallengeMapper;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingAttendanceRepository;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingRoundRepository;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingTermRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechBloggingChallengeService {
    private static final int MINIMUM_ROUND_DURATION_DAYS = 13; // 최소 2주(14일) 미만 체크
    private final TechBloggingRoundRepository roundRepository;
    private final TechBloggingTermRepository termRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final TechBloggingAttendanceRepository attendanceRepository;
    private final CustomLogger logger;

    // 챌린지 기간 생성 (회차 포함)
    @Transactional
    public TermDetailResponse createTerm(CreateTermRequest request) {
        validateNoDuplicateTerm(request.getYear(), request.isFirstHalf());

        TermPeriod period = TermPeriod.from(request.isFirstHalf());
        TechBloggingTerm term = TechBloggingTerm.create(request.getYear(), period);
        term = termRepository.save(term);

        DateRange periodDates = calculatePeriodDateRange(request.getYear(), period);
        generateRounds(
                term,
                periodDates.getStartDate(),
                periodDates.getEndDate(),
                request.getIntervalWeeks());

        return TechBloggingChallengeMapper.toTermDetailResponse(term);
    }

    // 챌린지 기간 조회
    @Transactional(readOnly = true)
    public TermDetailResponse getTerm(Long termId) {
        TechBloggingTerm term =
                termRepository
                        .findById(termId)
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());
        return TechBloggingChallengeMapper.toTermDetailResponse(term);
    }

    // 챌린지 기간 삭제
    @Transactional
    public void deleteTerm(Long termId) {
        TechBloggingTerm term =
                termRepository
                        .findById(termId)
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());
        term.softDelete();
    }

    // 단일 회차 생성
    @Transactional
    public RoundDetailResponse createRound(CreateSingleRoundRequest request) {
        TechBloggingTerm term =
                termRepository
                        .findById(request.getTermId())
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());

        validateNotPastDate(request.getStartDate());
        validateNoDuplicateRound(term, request.getStartDate(), request.getEndDate());

        TechBloggingRound round =
                TechBloggingRound.create(
                        request.getStartDate(), request.getEndDate(), request.getSequence(), term);
        round = roundRepository.save(round);
        term.addRound(round);

        return TechBloggingChallengeMapper.toRoundDetailResponse(round);
    }

    // 회차 수정
    @Transactional
    public RoundDetailResponse updateRound(UpdateRoundRequest request) {
        TechBloggingRound round =
                roundRepository
                        .findById(request.getRoundId())
                        .orElseThrow(() -> new TechBloggingRoundNotFoundException());

        long days =
                ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate().plusDays(1));
        if (days < MINIMUM_ROUND_DURATION_DAYS) {
            throw new TechBloggingRoundPeriodTooShortException();
        }

        round.updateDates(request.getStartDate(), request.getEndDate());
        return TechBloggingChallengeMapper.toRoundDetailResponse(round);
    }

    // 회차 삭제
    @Transactional
    public void deleteRound(Long roundId) {
        TechBloggingRound round =
                roundRepository
                        .findById(roundId)
                        .orElseThrow(() -> new TechBloggingRoundNotFoundException());
        round.softDelete();
    }

    // 모든 회차 조회
    @Transactional(readOnly = true)
    public RoundListResponse getRoundList() {
        List<TechBloggingRound> rounds = roundRepository.findByIsDeletedFalse();
        return TechBloggingChallengeMapper.toRoundListResponse(rounds);
    }

    private DateRange calculatePeriodDateRange(int year, TermPeriod period) {
        return DateRange.of(period.getStartDate(year), period.getEndDate(year));
    }

    private void generateRounds(
            TechBloggingTerm term, LocalDate start, LocalDate end, int interval) {
        int sequence = 1;
        int maxIterations = 100; // 무한루프 방지 안전장치
        int iterations = 0;

        while (!start.isAfter(end)) {
            if (++iterations > maxIterations) {
                throw new TechBloggingRoundInfiniteLoopException();
            }

            start = moveToNextWeekday(start);
            LocalDate roundEnd = start.plusWeeks(interval).minusDays(1);
            boolean isLastRound = roundEnd.isAfter(end);

            TechBloggingRound round = TechBloggingRound.create(start, roundEnd, sequence++, term);
            round = roundRepository.save(round);
            term.addRound(round);

            if (isLastRound) break;
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

    private void validateNoDuplicateTerm(int year, boolean firstHalf) {
        TermPeriod period = TermPeriod.from(firstHalf);
        if (termRepository.existsByYearAndPeriodAndIsDeletedFalse(year, period)) {
            throw new TechBloggingTermAlreadyExistsException();
        }
    }

    private void validateNoDuplicateRound(TechBloggingTerm term, LocalDate start, LocalDate end) {
        boolean exists = roundRepository.existsDuplicateRound(term, end, start);
        if (exists) {
            throw new TechBloggingRoundAlreadyExistsException();
        }
    }

    // 챌린지 지원
    @Transactional
    public void applyChallenge(Long userId, ApplyChallengeRequest request) {
        // 1. 해당 Term 조회
        TermPeriod period = TermPeriod.from(request.isFirstHalf());
        TechBloggingTerm term =
                termRepository
                        .findByYearAndPeriodAndIsDeletedFalse(request.getYear(), period)
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());

        // 2. 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        logger.info("user: {}", user);
        // 3. 이미 참여했는지 확인
        boolean alreadyJoined =
                term.getParticipants().stream()
                        .anyMatch(p -> !p.isDeleted() && p.getUser().getId().equals(userId));
        if (alreadyJoined) {
            throw new TechBloggingTermAlreadyJoinedException();
        }
        logger.info("alreadyJoined: {}", alreadyJoined);
        // 4. 참여자 저장
        TechBloggingTermParticipant participant = TechBloggingTermParticipant.create(term, user);
        term.getParticipants().add(participant);
        // CascadeType.ALL이므로 term 저장 시 participant도 저장됨
        termRepository.save(term);
        logger.info("term: {}", term);
    }

    // 출석 현황 조회
    @Transactional(readOnly = true)
    public List<AttendanceStatusResponse> getAttendanceStatus(Long termId) {
        TechBloggingTerm term = findTermByIdOrCurrent(termId);
        List<TechBloggingRound> rounds = getSortedRounds(term);
        List<User> participants = getActiveParticipants(term);

        return participants.stream()
                .map(
                        user -> {
                            List<Integer> sequence = new ArrayList<>();
                            int totalCount = 0;
                            for (TechBloggingRound round : rounds) {
                                int attendanceCount =
                                        attendanceRepository
                                                .countByUserAndTechBloggingRoundAndIsDeletedFalse(
                                                        user, round);
                                sequence.add(attendanceCount);
                                totalCount += attendanceCount;
                            }
                            return TechBloggingChallengeMapper.toAttendanceStatusResponse(
                                    user, sequence, totalCount);
                        })
                .toList();
    }

    // 스케줄러: 매일 새벽 4시 출석 체크
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void checkAttendance() {
        LocalDate today = LocalDate.now();
        // 진행중인 회차만 조회
        List<TechBloggingRound> rounds = roundRepository.findActiveRoundsOnDate(today);

        for (TechBloggingRound round : rounds) {
            List<User> participants =
                    round.getTerm().getParticipants().stream()
                            .filter(p -> !p.isDeleted())
                            .map(p -> p.getUser())
                            .toList();
            for (User user : participants) {
                java.util.List<Blog> blogs =
                        blogRepository.findByUserAndDateBetweenAndIsDeletedFalse(
                                user,
                                round.getStartDate().atStartOfDay(),
                                round.getEndDate().atTime(23, 59, 59));
                for (Blog blog : blogs) {
                    boolean alreadyExists =
                            attendanceRepository.existsByUserAndTechBloggingRoundAndBlog(
                                    user, round, blog);
                    if (!alreadyExists) {
                        TechBloggingAttendance attendance =
                                new TechBloggingAttendance(user, round, blog);
                        attendanceRepository.save(attendance);
                        System.out.printf(
                                "출석 저장: userId=%d, roundId=%d, blogId=%d\n",
                                user.getId(), round.getId(), blog.getId());
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public TermRoundsSummaryResponse getTermRoundsSummary(Long termId) {
        TechBloggingTerm term =
                termRepository
                        .findById(termId)
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());
        if (term.getRounds().isEmpty()) {
            throw new TechBloggingTermNoRoundsException();
        }
        DateRange termDateRange = calculateTermDateRange(term);
        return TechBloggingChallengeMapper.toTermRoundsSummaryResponse(
                term, termDateRange.getStartDate(), termDateRange.getEndDate());
    }

    @Transactional(readOnly = true)
    public List<TermSummaryResponse> getTermList() {
        List<TechBloggingTerm> terms = termRepository.findByIsDeletedFalse();
        return terms.stream()
                .map(
                        term -> {
                            DateRange termDateRange = calculateTermDateRange(term);
                            return TechBloggingChallengeMapper.toTermSummaryResponse(
                                    term, termDateRange.getStartDate(), termDateRange.getEndDate());
                        })
                .toList();
    }

    private DateRange calculateTermDateRange(TechBloggingTerm term) {
        LocalDate startDate =
                term.getRounds().stream()
                        .map(r -> r.getStartDate())
                        .min(LocalDate::compareTo)
                        .orElse(null);
        LocalDate endDate =
                term.getRounds().stream()
                        .map(r -> r.getEndDate())
                        .max(LocalDate::compareTo)
                        .orElse(null);
        return DateRange.of(startDate, endDate);
    }

    // 회차별 블로그 커서 기반 조회
    @Transactional(readOnly = true)
    public BlogChallengeListResponse getBlogsByRoundCursor(BlogChallengeCursorRequest request) {
        Long termId = request.termId();
        Long roundId = request.roundId();

        if (roundId != null && termId == null) {
            throw new TechBloggingRoundNotFoundException();
        }

        if (termId == null) {
            termId = findCurrentTermId();
        }

        if (roundId != null) {
            validateRoundBelongsToTerm(roundId, termId);
        }

        List<Long> validBlogIds = attendanceRepository.getValidBlogIds(termId, roundId);
        Blog cursorBlog = validateAndGetCursorBlog(request.cursorId(), validBlogIds);

        List<Blog> blogs =
                blogRepository.findBlogsForChallenge(
                        validBlogIds, cursorBlog, request.limit() + 1, request.sortBy());

        return TechBloggingChallengeMapper.toBlogChallengeListResponse(blogs, request.limit());
    }

    // 오늘 날짜 기준으로 현재 진행중인 termId 반환
    private Long findCurrentTermId() {
        LocalDate today = LocalDate.now();
        return termRepository.findByIsDeletedFalse().stream()
                .filter(
                        term ->
                                !term.isDeleted()
                                        && term.getRounds().stream()
                                                .anyMatch(
                                                        round ->
                                                                !round.isDeleted()
                                                                        && !round.getStartDate()
                                                                                .isAfter(today)
                                                                        && !round.getEndDate()
                                                                                .isBefore(today)))
                .findFirst()
                .map(term -> term.getId())
                .orElse(null);
    }

    private TechBloggingTerm findTermByIdOrCurrent(Long termId) {
        if (termId == null) {
            LocalDate today = LocalDate.now();
            return termRepository.findByIsDeletedFalse().stream()
                    .filter(
                            term ->
                                    !term.isDeleted()
                                            && term.getRounds().stream()
                                                    .anyMatch(
                                                            round ->
                                                                    !round.isDeleted()
                                                                            && !round.getStartDate()
                                                                                    .isAfter(today)
                                                                            && !round.getEndDate()
                                                                                    .isBefore(
                                                                                            today)))
                    .findFirst()
                    .orElseThrow(() -> new TechBloggingTermNotFoundException());
        } else {
            return termRepository
                    .findById(termId)
                    .orElseThrow(() -> new TechBloggingTermNotFoundException());
        }
    }

    private List<TechBloggingRound> getSortedRounds(TechBloggingTerm term) {
        return term.getRounds().stream()
                .filter(r -> !r.isDeleted())
                .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                .toList();
    }

    private List<User> getActiveParticipants(TechBloggingTerm term) {
        return term.getParticipants().stream()
                .filter(p -> !p.isDeleted())
                .map(p -> p.getUser())
                .toList();
    }

    private void validateRoundBelongsToTerm(Long roundId, Long termId) {
        TechBloggingRound round =
                roundRepository
                        .findById(roundId)
                        .orElseThrow(() -> new TechBloggingRoundNotFoundException());
        if (!round.getTerm().getId().equals(termId)) {
            throw new TechBloggingRoundNotFoundException();
        }
    }

    private Blog validateAndGetCursorBlog(Long cursorId, List<Long> validBlogIds) {
        if (cursorId != null) {
            if (!validBlogIds.contains(cursorId)) {
                throw new IllegalArgumentException("커서 blogId가 조건에 맞는 Attendance에 없습니다.");
            }
            return blogRepository
                    .findById(cursorId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 blogId입니다."));
        }
        return null;
    }
}
