package backend.techeerzip.domain.studyTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import java.util.List;

import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;

public interface StudyTeamDslRepository {
    List<StudySliceTeamsResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished);

    List<StudyTeam> sliceYoungTeamByCount(GetStudyTeamsQuery query);

    List<StudyTeam> sliceYoungTeamByDate(GetStudyTeamsQuery query);
    List<StudyTeam> sliceYoungTeam(GetStudyTeamsQuery query);
}
