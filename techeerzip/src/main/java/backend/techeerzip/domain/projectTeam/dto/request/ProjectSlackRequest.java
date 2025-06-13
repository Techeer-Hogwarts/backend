package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

        @NotNull private Long id;
        @NotNull private String type;

        @NotBlank
        @Size(max = 100)
        private String name;

        @NotBlank private String projectExplain;

        @Min(0)
        private int frontNum;

        @Min(0)
        private int backNum;

        @Min(0)
        private int dataEngNum;

        @Min(0)
        private int devOpsNum;

        @Min(0)
        private int fullStackNum;

        @NotNull private List<String> leader;
        @NotNull private List<String> email;
        @NotBlank private String recruitExplain;
        private String githubLink;
        private String notionLink;
        private List<String> stack;
    }

    @Getter
    @AllArgsConstructor
    public static class DM {

        @NotNull private Long teamId;
        @NotNull private String type;
        @NotNull private String teamName;
        @NotNull private String leaderEmail;
        @NotNull private String applicantEmail;
        @NotNull private String result;
    }
}
