package backend.techeerzip.domain.projectTeam.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;

public class TeamQueryMapper {
    private static final int DEFAULT_LIMIT = 10;

    private TeamQueryMapper() {}

    public static GetTeamsQuery mapToTeamsQuery(@NotNull GetTeamsQueryRequest request) {
        final SortType sortType = request.getSortType();
        return switch (sortType) {
            case UPDATE_AT_DESC -> mapWithDateCursor(sortType, request);
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC -> mapWithCountCursor(sortType, request);
        };
    }

    private static GetTeamsQuery mapWithDateCursor(SortType sortType, GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .id(req.getId())
                .dateCursor(Optional.ofNullable(req.getDateCursor()).orElse(LocalDateTime.now()))
                .countCursor(null)
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(sortType)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }

    private static GetTeamsQuery mapWithCountCursor(SortType sortType, GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .id(req.getId())
                .dateCursor(null)
                .countCursor(req.getCountCursor())
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(sortType)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }

    public static GetProjectTeamsQuery mapToProjectQuery(GetTeamsQueryRequest req) {
        final GetProjectTeamsQuery.GetProjectTeamsQueryBuilder builder =
                GetProjectTeamsQuery.builder()
                        .idCursor(req.getId())
                        .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                        .sortType(
                                Optional.ofNullable(req.getSortType())
                                        .orElse(SortType.UPDATE_AT_DESC))
                        .isRecruited(req.getIsRecruited())
                        .isFinished(req.getIsFinished())
                        .positionNumTypes(
                                PositionNumType.fromMany(
                                        Optional.ofNullable(req.getPositions()).orElse(List.of())));

        return switch (req.getSortType()) {
            case UPDATE_AT_DESC ->
                    builder.dateCursor(
                                    Optional.ofNullable(req.getDateCursor())
                                            .orElse(LocalDateTime.now()))
                            .build();
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC ->
                    builder.countCursor(req.getCountCursor()).build();
        };
    }

    public static GetStudyTeamsQuery mapToStudyQuery(GetTeamsQueryRequest req) {
        GetStudyTeamsQuery.GetStudyTeamsQueryBuilder builder =
                GetStudyTeamsQuery.builder()
                        .idCursor(req.getId())
                        .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                        .sortType(
                                Optional.ofNullable(req.getSortType())
                                        .orElse(SortType.UPDATE_AT_DESC))
                        .isRecruited(req.getIsRecruited())
                        .isFinished(req.getIsFinished());

        return switch (req.getSortType()) {
            case UPDATE_AT_DESC ->
                    builder.dateCursor(
                                    Optional.ofNullable(req.getDateCursor())
                                            .orElse(LocalDateTime.now()))
                            .build();
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC ->
                    builder.countCursor(req.getCountCursor()).build();
        };
    }

    public static List<SliceTeamsResponse> toTeamGetAllResponse(
            List<ProjectSliceTeamsResponse> projectResponses,
            List<StudySliceTeamsResponse> studyResponses,
            SortType sortType) {
        Comparator<SliceTeamsResponse> comparator =
                switch (sortType) {
                    case UPDATE_AT_DESC -> Comparator.comparing(SliceTeamsResponse::getUpdatedAt);
                    case VIEW_COUNT_DESC -> Comparator.comparing(SliceTeamsResponse::getViewCount);
                    case LIKE_COUNT_DESC -> Comparator.comparing(SliceTeamsResponse::getLikeCount);
                };

        PriorityQueue<SliceTeamsResponse> queue = new PriorityQueue<>(comparator.reversed());

        queue.addAll(projectResponses);
        queue.addAll(studyResponses);

        List<SliceTeamsResponse> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }

        return result;
    }
}
