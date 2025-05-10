package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;

public interface StudyTeamDslRepository {

    List<StudyTeamGetAllResponse> fetchStudyTeams(Boolean isRecruited, Boolean isFinished);
}
