package backend.techeerzip.domain.studyMember.repository;

import java.util.List;

import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;

public interface StudyMemberDslRepository {

    List<StudyApplicantResponse> findManyApplicants(Long studyTeamId);
}
