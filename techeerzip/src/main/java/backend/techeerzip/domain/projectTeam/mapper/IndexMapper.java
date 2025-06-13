package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.entity.User;

public class IndexMapper {

    private IndexMapper() {}

    public static IndexRequest.Project toProjectRequest(ProjectTeam team) {
        final List<String> resultImages =
                team.getResultImages().stream().map(ProjectResultImage::getImageUrl).toList();
        final List<String> teamStacks =
                team.getTeamStacks().stream().map(stack -> stack.getStack().getName()).toList();

        return IndexRequest.Project.builder()
                .id(String.valueOf(team.getId()))
                .name(team.getName())
                .projectExplain(team.getProjectExplain())
                .resultImages(resultImages)
                .teamStacks(teamStacks)
                .title(team.getName())
                .build();
    }

    public static IndexRequest.Study toStudyRequest(StudyTeam team) {
        final List<String> resultImages =
                team.getStudyResultImages().stream().map(StudyResultImage::getImageUrl).toList();

        return IndexRequest.Study.builder()
                .id(String.valueOf(team.getId()))
                .name(team.getName())
                .studyExplain(team.getStudyExplain())
                .resultImages(resultImages)
                .title(team.getName())
                .build();
    }

    public static IndexRequest.Stack toStackRequest(Stack stack) {
        return IndexRequest.Stack.builder()
                .id(String.valueOf(stack.getId()))
                .name(stack.getName())
                .category(stack.getCategory().name())
                .build();
    }

    public static IndexRequest.Resuem toResumeRequest(Resume resume, User user) {
        return IndexRequest.Resuem.builder()
                .id(String.valueOf(resume.getId()))
                .title(resume.getTitle())
                .url(resume.getUrl())
                .createdAt(resume.getCreatedAt().toString())
                .userId(String.valueOf(resume.getUser().getId()))
                .userProfileImage(resume.getUser().getProfileImage())
                .year(String.valueOf(user.getYear()))
                .position(user.getName())
                .build();
    }

    public static IndexRequest.User toUserRequest(User user) {
        return IndexRequest.User.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .school(user.getSchool())
                .email(user.getEmail())
                .year(String.valueOf(user.getYear()))
                .grade(user.getGrade())
                .stack(user.getStack())
                .profileImage(user.getProfileImage())
                .build();
    }
}
