package backend.techeerzip.domain.studyTeam.service;

import static backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepositoryImpl.ensureMaxSize;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.getNextInfo;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextInfo;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.studyTeam.repository.querydsl.StudyTeamDslRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {

    private final StudyTeamDslRepository studyTeamDslRepository;

    public GetAllTeamsResponse getYoungTeams(GetStudyTeamsQuery query) {
        final int limit = query.getLimit();
        final SortType sortType = query.getSortType();
        final List<StudyTeam> teams = studyTeamDslRepository.sliceYoungTeam(query);
        final SliceNextInfo next = setNextInfo(teams, limit, sortType);
        final List<StudyTeam> fitTeams = ensureMaxSize(teams, limit);
        final List<SliceTeamsResponse> responses = new ArrayList<>(
                fitTeams.stream().map(StudyTeamMapper::toGetAllResponse).toList());
        return new GetAllTeamsResponse(responses, next);
    }

    public List<StudySliceTeamsResponse> getYoungTeamsById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        return studyTeamDslRepository.findManyYoungTeamById(keys, isRecruited, isFinished);
    }

    private static SliceNextInfo setNextInfo(List<StudyTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            return SliceNextInfo.builder().hasNext(false).build();
        }
        final StudyTeam last = sortedTeams.getLast();
        return getNextInfo(sortType, last.getId(), last.getUpdatedAt(), last.getViewCount(),
                last.getLikeCount());
    }
}
