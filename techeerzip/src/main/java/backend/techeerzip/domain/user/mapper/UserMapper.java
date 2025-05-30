package backend.techeerzip.domain.user.mapper;

import org.springframework.stereotype.Component;

import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.User;

@Component
public class UserMapper {

    public GetUserResponse toGetUserResponse(User user) {
        return GetUserResponse.builder()
                .id(user.getId())
                .profileImage(user.getProfileImage())
                .name(user.getName())
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
                .isLft(user.isLft())
                .year(user.getYear())
                .stack(user.getStack())
                .projectTeams(
                        user.getProjectMembers().stream()
                                .filter(pm -> pm.getProjectTeam() != null)
                                .map(
                                        pm ->
                                                GetUserResponse.ProjectTeamDTO.builder()
                                                        .id(pm.getProjectTeam().getId())
                                                        .name(pm.getProjectTeam().getName())
                                                        .resultImages(
                                                                pm
                                                                        .getProjectTeam()
                                                                        .getResultImages()
                                                                        .stream()
                                                                        .map(
                                                                                img ->
                                                                                        img
                                                                                                .getImageUrl())
                                                                        .toList())
                                                        .mainImage(
                                                                pm.getProjectTeam()
                                                                                .getMainImages()
                                                                                .isEmpty()
                                                                        ? ""
                                                                        : pm.getProjectTeam()
                                                                                .getMainImages()
                                                                                .get(0)
                                                                                .getImageUrl())
                                                        .build())
                                .toList())
                .studyTeams(
                        user.getStudyMembers().stream()
                                .filter(sm -> sm.getStudyTeam() != null)
                                .map(
                                        sm ->
                                                GetUserResponse.StudyTeamDTO.builder()
                                                        .id(sm.getStudyTeam().getId())
                                                        .name(sm.getStudyTeam().getName())
                                                        .resultImages(
                                                                sm
                                                                        .getStudyTeam()
                                                                        .getStudyResultImages()
                                                                        .stream()
                                                                        .map(
                                                                                img ->
                                                                                        img
                                                                                                .getImageUrl())
                                                                        .toList())
                                                        .mainImage(
                                                                sm.getStudyTeam()
                                                                                .getStudyResultImages()
                                                                                .isEmpty()
                                                                        ? ""
                                                                        : sm.getStudyTeam()
                                                                                .getStudyResultImages()
                                                                                .get(0)
                                                                                .getImageUrl())
                                                        .build())
                                .toList())
                .experiences(
                        user.getExperiences().stream()
                                .map(
                                        exp ->
                                                GetUserResponse.ExperienceDTO.builder()
                                                        .id(exp.getId())
                                                        .position(exp.getPosition())
                                                        .companyName(exp.getCompanyName())
                                                        .startDate(exp.getStartDate().toString())
                                                        .endDate(
                                                                exp.getEndDate() != null
                                                                        ? exp.getEndDate()
                                                                                .toString()
                                                                        : null)
                                                        .category(exp.getCategory())
                                                        .isFinished(exp.isFinished())
                                                        .description(exp.getDescription())
                                                        .build())
                                .toList())
                .build();
    }
}
