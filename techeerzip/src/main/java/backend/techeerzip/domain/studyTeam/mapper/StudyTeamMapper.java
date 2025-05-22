package backend.techeerzip.domain.studyTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public class StudyTeamMapper {

    private StudyTeamMapper() {
        // static 유틸 클래스
    }

    public static StudyTeamGetAllResponse toGetAllResponse(StudyTeam studyTeam) {
        return StudyTeamGetAllResponse.builder()
                .id(studyTeam.getId())
                .name(studyTeam.getName())
                .studyExplain(studyTeam.getStudyExplain())
                .isDeleted(studyTeam.getIsDeleted())
                .isFinished(studyTeam.getIsFinished())
                .isRecruited(studyTeam.getIsRecruited())
                .recruitNum(studyTeam.getRecruitNum())
                .createdAt(studyTeam.getCreatedAt())
                .build();
    }
}
