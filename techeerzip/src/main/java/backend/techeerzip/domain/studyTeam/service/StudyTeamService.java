package backend.techeerzip.domain.studyTeam.service;

import static backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepositoryImpl.ensureMaxSize;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.getNextInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;
import backend.techeerzip.domain.studyTeam.repository.StudyTeamRepository;
import backend.techeerzip.domain.studyTeam.repository.querydsl.StudyTeamDslRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {

    private final StudyTeamDslRepository studyTeamDslRepository;
    private final StudyTeamRepository studyTeamRepository;

    public GetAllTeamsResponse getSliceTeams(GetStudyTeamsQuery query) {
        final int limit = query.getLimit();
        final SortType sortType = query.getSortType();
        final List<StudyTeam> teams = studyTeamDslRepository.sliceTeams(query);
        final SliceNextCursor next = setNextInfo(teams, limit, sortType);
        final List<StudyTeam> fitTeams = ensureMaxSize(teams, limit);
        final List<SliceTeamsResponse> responses =
                new ArrayList<>(fitTeams.stream().map(StudyTeamMapper::toGetAllResponse).toList());
        return new GetAllTeamsResponse(responses, next);
    }

    public List<StudySliceTeamsResponse> getYoungTeamsById(List<Long> keys) {
        final List<StudyTeam> teams = studyTeamRepository.findAllById(keys);
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }

    private static SliceNextCursor setNextInfo(
            List<StudyTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            return SliceNextCursor.builder().hasNext(false).build();
        }
        final StudyTeam last = sortedTeams.getLast();
        return getNextInfo(
                sortType,
                last.getId(),
                last.getUpdatedAt(),
                last.getViewCount(),
                last.getLikeCount());
    }
}
