package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;

public interface StudyTeamDslRepository {

    List<StudyTeamGetAllResponse> sliceYoungTeam(
            Boolean isRecruited, Boolean isFinished, Long limit);

    List<StudyTeamGetAllResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished);
}
