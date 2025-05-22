package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ProjectSlackRequest {

    private ProjectSlackRequest() {}

    @Getter
    @AllArgsConstructor
    public static class Channel {

        private Long id;
        private TeamType type;
        private String name;
        private String projectExplain;
        private int frontNum;
        private int backNum;
        private int dataEngNum;
        private int devOpsNum;
        private int fullStackNum;
        private List<String> leader;
        private List<String> email;
        private String recruitExplain;
        private String githubLink;
        private String notionLink;
        private List<String> stack;
    }

    @Getter
    @AllArgsConstructor
    public static class DM {

        private Long teamId;
        private TeamType type;
        private String teamName;
        private String leaderEmail;
        private String applicantEmail;
        private StatusCategory result;
    }
}
