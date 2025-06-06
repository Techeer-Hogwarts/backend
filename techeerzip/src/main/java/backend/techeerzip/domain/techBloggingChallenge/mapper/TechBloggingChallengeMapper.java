package backend.techeerzip.domain.techBloggingChallenge.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.AttendanceStatusResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.BlogChallengeListResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.BlogChallengeSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundListResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermRoundsSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;
import backend.techeerzip.domain.user.entity.User;

public class TechBloggingChallengeMapper {
    private TechBloggingChallengeMapper() {
        throw new IllegalStateException("TechBloggingChallengeMapper is a utility class");
    }

    public static TermDetailResponse toTermDetailResponse(TechBloggingTerm term) {
        List<RoundDetailResponse> rounds = term.getRounds().stream()
                .map(TechBloggingChallengeMapper::toRoundDetailResponse)
                .collect(Collectors.toList());

        return TermDetailResponse.builder()
                .termId(term.getId())
                .termName(term.getTermName())
                .year(term.getYear())
                .firstHalf(term.isFirstHalf())
                .createdAt(term.getCreatedAt())
                .rounds(rounds)
                .build();
    }

    public static RoundDetailResponse toRoundDetailResponse(TechBloggingRound round) {
        return RoundDetailResponse.builder()
                .roundId(round.getId())
                .termId(round.getTerm().getId())
                .roundName(round.getRoundName())
                .termName(round.getTerm().getTermName())
                .sequence(round.getSequence())
                .startDate(round.getStartDate())
                .endDate(round.getEndDate())
                .year(round.getYear())
                .firstHalf(round.isFirstHalf())
                .build();
    }

    public static RoundListResponse toRoundListResponse(List<TechBloggingRound> rounds) {
        List<RoundDetailResponse> roundDetails = rounds.stream()
                .map(TechBloggingChallengeMapper::toRoundDetailResponse)
                .collect(Collectors.toList());
        return RoundListResponse.builder()
                .rounds(roundDetails)
                .build();
    }

    public static TermSummaryResponse toTermSummaryResponse(TechBloggingTerm term, LocalDate startDate,
            LocalDate endDate) {
        return TermSummaryResponse.builder()
                .termId(term.getId())
                .termName(term.getTermName())
                .startDate(startDate)
                .endDate(endDate)
                .totalRounds(term.getRounds().size())
                .build();
    }

    public static TermRoundsSummaryResponse toTermRoundsSummaryResponse(
            TechBloggingTerm term, LocalDate startDate, LocalDate endDate) {
        List<TermRoundsSummaryResponse.RoundSummary> rounds = term.getRounds().stream()
                .sorted((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
                .map(round -> TermRoundsSummaryResponse.RoundSummary.builder()
                        .roundId(round.getId())
                        .name(round.getSequence() + "회차")
                        .period(round.getStartDate() + " - " + round.getEndDate())
                        .build())
                .collect(Collectors.toList());

        return TermRoundsSummaryResponse.builder()
                .termId(term.getId())
                .termName(term.getTermName())
                .startDate(startDate)
                .endDate(endDate)
                .rounds(rounds)
                .build();
    }

    public static AttendanceStatusResponse toAttendanceStatusResponse(
            User user, List<Integer> sequence, int totalCount) {
        return AttendanceStatusResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .sequence(sequence)
                .totalCount(totalCount)
                .build();
    }

    public static BlogChallengeSummaryResponse toBlogChallengeSummaryResponse(Blog blog) {
        return BlogChallengeSummaryResponse.builder()
                .blogId(blog.getId())
                .title(blog.getTitle())
                .url(blog.getUrl())
                .author(blog.getUser().getName())
                .createdAt(blog.getCreatedAt())
                .viewCount(blog.getViewCount())
                .likeCount(blog.getLikeCount())
                .build();
    }

    public static BlogChallengeListResponse toBlogChallengeListResponse(
            List<Blog> blogs, int limit) {
        List<BlogChallengeSummaryResponse> blogResponses = blogs.stream()
                .map(TechBloggingChallengeMapper::toBlogChallengeSummaryResponse)
                .collect(Collectors.toList());

        boolean hasNext = blogResponses.size() > limit;
        Long nextCursor = hasNext ? blogResponses.get(limit - 1).getBlogId() : null;

        return BlogChallengeListResponse.builder()
                .data(hasNext ? blogResponses.subList(0, limit) : blogResponses)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }
}