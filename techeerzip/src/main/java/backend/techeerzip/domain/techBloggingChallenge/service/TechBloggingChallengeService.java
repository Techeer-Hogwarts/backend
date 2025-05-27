package backend.techeerzip.domain.techBloggingChallenge.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.*;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.*;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingAttendance;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTermParticipant;
import backend.techeerzip.domain.techBloggingChallenge.exception.*;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingAttendanceRepository;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingRoundRepository;
import backend.techeerzip.domain.techBloggingChallenge.repository.TechBloggingTermRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
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

    // 챌린지 기간 생성 (회차 포함)
    @Transactional
    public TermDetailResponse createTerm(CreateTermRequest request) {
        // 1. 기존 기간 확인
        validateNoDuplicateTerm(request.getYear(), request.isFirstHalf());

        // 2. 기간 생성
        TechBloggingTerm term = TechBloggingTerm.create(request.getYear(), request.isFirstHalf());
        term = termRepository.save(term);

        // 3. 회차 생성
        LocalDate[] periodDates = calculatePeriodDates(request.getYear(), request.isFirstHalf());
        generateRounds(term, periodDates[0], periodDates[1], request.getIntervalWeeks());

        // 4. 응답 생성
        return convertToTermDetailResponse(term);
    }

    // 챌린지 기간 조회
    @Transactional(readOnly = true)
    public TermDetailResponse getTerm(Long termId) {
        TechBloggingTerm term =
                termRepository
                        .findById(termId)
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());
        return convertToTermDetailResponse(term);
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

        return convertToRoundDetailResponse(round);
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
        return convertToRoundDetailResponse(round);
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
        List<RoundDetailResponse> roundDetails =
                roundRepository.findByIsDeletedFalse().stream()
                        .map(this::convertToRoundDetailResponse)
                        .collect(Collectors.toList());
        return new RoundListResponse(roundDetails);
    }

    private LocalDate[] calculatePeriodDates(int year, boolean firstHalf) {
        LocalDate start = firstHalf ? LocalDate.of(year, 3, 1) : LocalDate.of(year, 9, 1);
        LocalDate end =
                firstHalf
                        ? LocalDate.of(year, 7, 31)
                        : LocalDate.of(year + 1, 2, YearMonth.of(year + 1, 2).lengthOfMonth());
        return new LocalDate[] {start, end};
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
        if (termRepository.existsByYearAndFirstHalfAndIsDeletedFalse(year, firstHalf)) {
            throw new TechBloggingTermAlreadyExistsException();
        }
    }

    private void validateNoDuplicateRound(TechBloggingTerm term, LocalDate start, LocalDate end) {
        boolean exists = roundRepository.existsDuplicateRound(term, end, start);
        if (exists) {
            throw new TechBloggingRoundAlreadyExistsException();
        }
    }

    private TermDetailResponse convertToTermDetailResponse(TechBloggingTerm term) {
        List<RoundDetailResponse> rounds =
                term.getRounds().stream()
                        .map(this::convertToRoundDetailResponse)
                        .collect(Collectors.toList());

        return new TermDetailResponse(
                term.getId(),
                term.getTermName(),
                term.getYear(),
                term.isFirstHalf(),
                term.getCreatedAt(),
                rounds);
    }

    private RoundDetailResponse convertToRoundDetailResponse(TechBloggingRound round) {
        return new RoundDetailResponse(
                round.getId(),
                round.getTerm().getId(),
                round.getRoundName(),
                round.getTerm().getTermName(),
                round.getSequence(),
                round.getStartDate(),
                round.getEndDate(),
                round.getYear(),
                round.isFirstHalf());
    }

    // 챌린지 지원
    @Transactional
    public void applyChallenge(Long userId, ApplyChallengeRequest request) {
        // 1. 해당 Term 조회
        TechBloggingTerm term =
                termRepository
                        .findByYearAndFirstHalfAndIsDeletedFalse(
                                request.getYear(), request.isFirstHalf())
                        .orElseThrow(() -> new TechBloggingTermNotFoundException());

        // 2. 유저 조회
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        // 3. 이미 참여했는지 확인
        boolean alreadyJoined =
                term.getParticipants().stream()
                        .anyMatch(p -> !p.isDeleted() && p.getUser().getId().equals(userId));
        if (alreadyJoined) {
            throw new TechBloggingTermAlreadyJoinedException();
        }

        // 4. 참여자 저장
        TechBloggingTermParticipant participant = TechBloggingTermParticipant.create(term, user);
        term.getParticipants().add(participant);
        // CascadeType.ALL이므로 term 저장 시 participant도 저장됨
        termRepository.save(term);
    }

    // 회차별 블로그 조회
    @Transactional(readOnly = true)
    public void getBlogsByRound(Long roundId, String sortOption) {
        // TODO: 회차별 블로그 목록 조회 (최신순, 조회순, 이름순)
    }

    // 출석 현황 조회
    @Transactional(readOnly = true)
    public List<AttendanceStatusResponse> getAttendanceStatus(Long termId) {
        TechBloggingTerm term;
        if (termId == null) {
            // 현재 진행중인 term: 오늘 날짜가 포함된 term
            LocalDate today = LocalDate.now();
            term =
                    termRepository.findAll().stream()
                            .filter(
                                    t ->
                                            !t.isDeleted()
                                                    && t.getRounds().stream()
                                                            .anyMatch(
                                                                    r ->
                                                                            !r.isDeleted()
                                                                                    && !r.getStartDate()
                                                                                            .isAfter(
                                                                                                    today)
                                                                                    && !r.getEndDate()
                                                                                            .isBefore(
                                                                                                    today)))
                            .findFirst()
                            .orElseThrow(() -> new TechBloggingTermNotFoundException());
        } else {
            term =
                    termRepository
                            .findById(termId)
                            .orElseThrow(() -> new TechBloggingTermNotFoundException());
        }

        // 회차를 순서대로 정렬
        List<TechBloggingRound> rounds =
                term.getRounds().stream()
                        .filter(r -> !r.isDeleted())
                        .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                        .collect(java.util.stream.Collectors.toList());

        // 참여자 목록
        List<User> participants =
                term.getParticipants().stream()
                        .filter(p -> !p.isDeleted())
                        .map(p -> p.getUser())
                        .collect(java.util.stream.Collectors.toList());

        List<AttendanceStatusResponse> result = new java.util.ArrayList<>();
        for (User user : participants) {
            List<Integer> sequence = new java.util.ArrayList<>();
            int totalCount = 0;
            for (TechBloggingRound round : rounds) {
                int attendanceCount =
                        attendanceRepository.countByUserAndTechBloggingRoundAndIsDeletedFalse(
                                user, round);
                sequence.add(attendanceCount);
                totalCount += attendanceCount;
            }
            result.add(
                    new AttendanceStatusResponse(
                            user.getId(), user.getName(), sequence, totalCount));
        }
        return result;
    }

    // 스케줄러: 매일 새벽 4시 출석 체크
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    @Transactional
    public void checkAttendance() {
        LocalDate today = LocalDate.now();
        // 진행중인 회차만 조회
        List<TechBloggingRound> rounds =
                roundRepository
                        .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
                                today, today);

        for (TechBloggingRound round : rounds) {
            List<User> participants =
                    round.getTerm().getParticipants().stream()
                            .filter(p -> !p.isDeleted())
                            .map(p -> p.getUser())
                            .collect(java.util.stream.Collectors.toList());
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
        List<TermRoundsSummaryResponse.RoundSummary> rounds =
                term.getRounds().stream()
                        .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                        .map(
                                r ->
                                        new TermRoundsSummaryResponse.RoundSummary(
                                                r.getId(),
                                                r.getSequence() + "회차",
                                                r.getStartDate() + " - " + r.getEndDate()))
                        .collect(java.util.stream.Collectors.toList());
        return new TermRoundsSummaryResponse(
                term.getId(), term.getTermName(), startDate, endDate, rounds);
    }

    @Transactional(readOnly = true)
    public List<TermSummaryResponse> getTermList() {
        List<TechBloggingTerm> terms = termRepository.findAll();
        return terms.stream()
                .map(
                        term -> {
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
                            int totalRounds = term.getRounds().size();
                            return new TermSummaryResponse(
                                    term.getId(),
                                    term.getTermName(),
                                    startDate,
                                    endDate,
                                    totalRounds);
                        })
                .collect(java.util.stream.Collectors.toList());
    }

    // 회차별 블로그 커서 기반 조회
    @Transactional(readOnly = true)
    public BlogChallengeListResponse getBlogsByRoundCursor(BlogChallengeCursorRequest request) {
        String sort =
                (request.getSort() == null || request.getSort().isBlank())
                        ? "latest"
                        : request.getSort();

        Long termId = request.getTermId();
        if (termId == null) {
            termId = findCurrentTermId();
        } else {
            termRepository
                    .findById(termId)
                    .orElseThrow(() -> new TechBloggingTermNotFoundException());
        }
        Long roundId = request.getRoundId();

        // 1. Attendance에서 조건에 맞는 blogId 추출
        List<Long> validBlogIds = attendanceRepository.getValidBlogIds(termId, roundId);

        // 2. 커서 blogId가 조건에 맞는지 확인
        Blog cursorBlog = null;
        if (request.getCursorId() != null) {
            if (!validBlogIds.contains(request.getCursorId())) {
                throw new IllegalArgumentException("커서 blogId가 조건에 맞는 Attendance에 없습니다.");
            }
            cursorBlog =
                    blogRepository
                            .findById(request.getCursorId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 blogId입니다."));
        }

        // 3. Blog 테이블에서 정렬/커서 조건 적용해서 조회
        List<Blog> blogs =
                blogRepository.findBlogsForChallenge(
                        validBlogIds, sort, cursorBlog, request.getLimit() + 1);

        List<BlogChallengeSummaryResponse> blogResponses =
                blogs.stream()
                        .map(
                                blog ->
                                        new BlogChallengeSummaryResponse(
                                                blog.getId(),
                                                blog.getTitle(),
                                                blog.getUrl(),
                                                blog.getUser().getName(),
                                                blog.getCreatedAt(),
                                                blog.getViewCount(),
                                                blog.getLikeCount()))
                        .toList();

        return new BlogChallengeListResponse(blogResponses, request.getLimit());
    }

    // 오늘 날짜 기준으로 현재 진행중인 termId 반환
    private Long findCurrentTermId() {
        LocalDate today = LocalDate.now();
        return termRepository.findAll().stream()
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
}
