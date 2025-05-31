package backend.techeerzip.domain.studyTeam.mapper;

import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public class StudyTeamMapper {

    private StudyTeamMapper() {
        // static 유틸 클래스
    }

    public static StudySliceTeamsResponse toGetAllResponse(StudyTeam studyTeam) {
        return StudySliceTeamsResponse.builder()
                .id(studyTeam.getId())
                .name(studyTeam.getName())
                .studyExplain(studyTeam.getStudyExplain())
                .isDeleted(studyTeam.getIsDeleted())
                .isFinished(studyTeam.getIsFinished())
                .isRecruited(studyTeam.getIsRecruited())
                .recruitNum(studyTeam.getRecruitNum())
                .viewCount(studyTeam.getViewCount())
                .likeCount(studyTeam.getLikeCount())
                .updatedAt(studyTeam.getUpdatedAt())
                .createdAt(studyTeam.getCreatedAt())
                .build();
    }
}
