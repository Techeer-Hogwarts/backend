package backend.techeerzip.domain.studyTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;

public class StudyTeamMapper {

    private StudyTeamMapper() {
        // static 유틸 클래스
    }

    public static StudyTeamGetAllResponse toGetAllResponse(
            backend.techeerzip.domain.studyTeam.entity.StudyTeam studyTeam) {
        return StudyTeamGetAllResponse.builder()
                .id(studyTeam.getId())
                .name(studyTeam.getName())
                .studyExplain(studyTeam.getStudyExplain())
                .isDeleted(studyTeam.isDeleted())
                .isFinished(studyTeam.isFinished())
                .isRecruited(studyTeam.isRecruited())
                .recruitNum(studyTeam.getRecruitNum())
                .createdAt(studyTeam.getCreatedAt())
                .build();
    }
}
