package backend.techeerzip.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonPropertyOrder({
    "id",
    "profileImage",
    "name",
    "roleId",
    "nickname",
    "email",
    "school",
    "grade",
    "mainPosition",
    "subPosition",
    "githubUrl",
    "mediumUrl",
    "velogUrl",
    "tistoryUrl",
    "isLft",
    "year",
    "stack",
    "projectTeams",
    "studyTeams",
    "experiences"
})
@Getter
@Builder
@AllArgsConstructor
public class GetUserResponse {
    private final Long id;
    private final String profileImage;
    private final String name;
    private final Integer roleId;
    private final String nickname;
    private final String email;
    private final String school;
    private final String grade;
    private final String mainPosition;
    private final String subPosition;
    private final String githubUrl;
    private final String mediumUrl;
    private final String velogUrl;
    private final String tistoryUrl;
    private final Boolean isLft;
    private final int year;
    private final List<String> stack;

    private final List<ProjectTeamDTO> projectTeams;
    private final List<StudyTeamDTO> studyTeams;
    private final List<ExperienceDTO> experiences;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProjectTeamDTO {
        private final Long id;
        private final String name;
        private final List<String> resultImages;
        private final String mainImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class StudyTeamDTO {
        private final Long id;
        private final String name;
        private final List<String> resultImages;
        private final String mainImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExperienceDTO {
        private final Long id;
        private final String position;
        private final String companyName;
        private final String startDate;
        private final String endDate;
        private final String category;
        private final boolean isFinished;
        private final String description;
    }
}
