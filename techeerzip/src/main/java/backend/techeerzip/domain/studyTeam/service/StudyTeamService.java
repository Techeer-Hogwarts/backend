package backend.techeerzip.domain.studyTeam.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.repository.StudyTeamRepository;
import backend.techeerzip.domain.studyTeam.repository.querydsl.StudyTeamDslRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {

    private final StudyTeamRepository studyTeamRepository;
    private final StudyTeamDslRepository studyTeamDslRepository;

    public List<StudyTeamGetAllResponse> getAllTeams(GetTeamQueryRequest request) {
        final List<TeamType> teamTypes = request.getTeamTypes();
        final Boolean isRecruited = request.getIsRecruited();
        final Boolean isFinished = request.getIsFinished();
        if (teamTypes != null && !teamTypes.contains(TeamType.STUDY)) {
            return Collections.emptyList();
        }
        return studyTeamDslRepository.fetchStudyTeams(isRecruited, isFinished);
    }
}
