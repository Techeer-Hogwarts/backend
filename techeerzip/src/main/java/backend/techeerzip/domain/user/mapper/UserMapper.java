package backend.techeerzip.domain.user.mapper;

import org.springframework.stereotype.Component;

import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

        public GetUserResponse toGetUserResponse(User user) {
                return GetUserResponse.builder()
                                .id(user.getId())
                                .profileImage(user.getProfileImage())
                                .name(user.getName())
                                .roleId(user.getRole().getId())
                                .nickname(user.getNickname())
                                .email(user.getEmail())
                                .school(user.getSchool())
                                .grade(user.getGrade())
                                .mainPosition(user.getMainPosition())
                                .subPosition(user.getSubPosition())
                                .githubUrl(user.getGithubUrl())
                                .mediumUrl(user.getMediumUrl())
                                .velogUrl(user.getVelogUrl())
                                .tistoryUrl(user.getTistoryUrl())
                                .isLft(Boolean.TRUE.equals(user.isLft()))
                                .year(user.getYear() != null ? user.getYear() : -1)
                                .stack(user.getStack())
                                .projectTeams(user.getProjectMembers().stream()
                                                .filter(pm -> !pm.isDeleted()
                                                                && !pm.getProjectTeam().isDeleted()
                                                                && pm.getStatus() == StatusCategory.APPROVED
                                                                && pm.getUser().getId().equals(user.getId()))
                                                .map(pm -> GetUserResponse.ProjectTeamDTO.builder()
                                                                .id(pm.getProjectTeam().getId())
                                                                .name(pm.getProjectTeam().getName())
                                                                .resultImages(pm.getProjectTeam().getResultImages()
                                                                                .stream()
                                                                                .map(img -> img.getImageUrl())
                                                                                .toList())
                                                                .mainImage(pm.getProjectTeam().getMainImages().isEmpty()
                                                                                ? ""
                                                                                : pm.getProjectTeam().getMainImages()
                                                                                                .get(0).getImageUrl())
                                                                .build())
                                                .toList())
                                .studyTeams(user.getStudyMembers().stream()
                                                .filter(sm -> !sm.isDeleted()
                                                                && !sm.getStudyTeam().isDeleted()
                                                                && sm.getStatus() == StatusCategory.APPROVED
                                                                && sm.getUser().getId().equals(user.getId()))
                                                .map(sm -> GetUserResponse.StudyTeamDTO.builder()
                                                                .id(sm.getStudyTeam().getId())
                                                                .name(sm.getStudyTeam().getName())
                                                                .resultImages(sm.getStudyTeam().getStudyResultImages()
                                                                                .stream()
                                                                                .map(img -> img.getImageUrl())
                                                                                .toList())
                                                                .mainImage(sm.getStudyTeam().getStudyResultImages()
                                                                                .isEmpty()
                                                                                                ? ""
                                                                                                : sm.getStudyTeam()
                                                                                                                .getStudyResultImages()
                                                                                                                .get(0)
                                                                                                                .getImageUrl())
                                                                .build())
                                                .toList())
                                .experiences(user.getExperiences().stream()
                                                .filter(e -> !e.isDeleted())
                                                .map(e -> GetUserResponse.ExperienceDTO.builder()
                                                                .id(e.getId())
                                                                .position(e.getPosition())
                                                                .companyName(e.getCompanyName())
                                                                .startDate(e.getStartDate().toString())
                                                                .endDate(e.getEndDate() != null
                                                                                ? e.getEndDate().toString()
                                                                                : null)
                                                                .category(e.getCategory())
                                                                .isFinished(e.isFinished())
                                                                .description(e.getDescription())
                                                                .build())
                                                .toList())
                                .build();
        }
}
