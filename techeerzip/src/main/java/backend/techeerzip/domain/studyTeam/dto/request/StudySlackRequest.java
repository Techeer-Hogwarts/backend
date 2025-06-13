package backend.techeerzip.domain.studyTeam.dto.request;

import java.util.List;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StudySlackRequest {
    private StudySlackRequest() {}

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Channel {

        private Long id;
        private String type;
        private String name;
        private String studyExplain;
        private List<String> leader;
        private List<String> email;
        private String recruitExplain;
        private String notionLink;
        private String githubLink;
        private String goal;
        private String rule;
        private Integer recruitNum;
    }

    @Getter
    @AllArgsConstructor
    public static class DM {

        private Long teamId;
        private String type;
        private String teamName;
        private String leaderEmail;
        private String applicantEmail;
        private String result;
    }
}
