package backend.techeerzip.domain.studyTeam.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.studyTeam.repository.StudyTeamRepository;
import backend.techeerzip.domain.studyTeam.repository.querydsl.StudyTeamDslRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {

    private final StudyTeamRepository studyTeamRepository;
    private final StudyTeamDslRepository studyTeamDslRepository;

    public List<StudyTeamGetAllResponse> getYoungTeams(
            Boolean isRecruited, Boolean isFinished, Long limit) {
        return studyTeamDslRepository.sliceYoungTeam(isRecruited, isFinished, limit);
    }

    public List<StudyTeamGetAllResponse> getYoungTeamsById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        return studyTeamDslRepository.findManyYoungTeamById(keys, isRecruited, isFinished);
    }
}
