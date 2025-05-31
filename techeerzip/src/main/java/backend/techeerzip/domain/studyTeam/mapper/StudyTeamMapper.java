package backend.techeerzip.domain.studyTeam.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;

@Component
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

    public GetUserResponse.StudyTeamDTO toUserStudyTeamDTO(StudyMember sm) {
        if (sm.getStudyTeam() == null) return null;

        StudyTeam studyTeam = sm.getStudyTeam();

        return GetUserResponse.StudyTeamDTO.builder()
                .id(studyTeam.getId())
                .name(studyTeam.getName())
                .resultImages(
                        studyTeam.getStudyResultImages().stream()
                                .map(img -> img.getImageUrl())
                                .collect(Collectors.toList()))
                .mainImage(
                        studyTeam.getStudyResultImages().isEmpty()
                                ? ""
                                : studyTeam.getStudyResultImages().get(0).getImageUrl())
                .build();
    }

    public List<GetUserResponse.StudyTeamDTO> toUserStudyTeamDTOList(List<StudyMember> members) {
        return members.stream()
                .map(this::toUserStudyTeamDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
