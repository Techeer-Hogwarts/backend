package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public interface StudyTeamDslRepository {
    List<StudyTeam> sliceTeamsByCount(GetStudyTeamsQuery query);

    List<StudyTeam> sliceTeamsByDate(GetStudyTeamsQuery query);

    List<StudyTeam> sliceTeams(GetStudyTeamsQuery query);
}
