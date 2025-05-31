package backend.techeerzip.domain.user.mapper;

import org.springframework.stereotype.Component;

import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.userExperience.mapper.UserExperienceMapper;

@Component
public class UserMapper {

    private final ProjectTeamMapper projectTeamMapper;
    private final StudyTeamMapper studyTeamMapper;
    private final UserExperienceMapper userExperienceMapper;

    public UserMapper(
            ProjectTeamMapper projectTeamMapper,
            StudyTeamMapper studyTeamMapper,
            UserExperienceMapper userExperienceMapper) {
        this.projectTeamMapper = projectTeamMapper;
        this.studyTeamMapper = studyTeamMapper;
        this.userExperienceMapper = userExperienceMapper;
    }

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
                .projectTeams(projectTeamMapper.toUserProjectTeamDTOList(user.getProjectMembers()))
                .studyTeams(studyTeamMapper.toUserStudyTeamDTOList(user.getStudyMembers()))
                .experiences(userExperienceMapper.toDtoList(user.getExperiences()))
                .build();
    }
}
