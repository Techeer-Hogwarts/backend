package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectTeamDetailResponse {

    private Long id;
    private boolean isDeleted;
    private boolean isRecruited;
    private boolean isFinished;

    private String name;
    private String githubLink;
    private String notionLink;
    private String projectExplain;
    private int frontendNum;
    private int backendNum;
    private int devopsNum;
    private int fullStackNum;
    private int dataEngineerNum;
    private String recruitExplain;

    private List<ResultImageInfo> resultImages;
    private List<MainImageInfo> mainImages;
    private List<TeamStackDetail> teamStacks;
    private List<ProjectMemberInfo> projectMember;

    @Getter
    @Builder
    public static class ResultImageInfo {

        private Long id;
        private boolean isDeleted;
        private String imageUrl;
    }

    @Getter
    @Builder
    public static class MainImageInfo {

        private Long id;
        private boolean isDeleted;
        private String imageUrl;
    }

    @Getter
    @Builder
    public static class TeamStackDetail {

        private Long id;
        private boolean isDeleted;
        private Long projectTeamId;
        private boolean isMain;
        private StackInfo stack;

        @Getter
        @Builder
        public static class StackInfo {

            private String name;
            private String category;
        }
    }

    @Getter
    @Builder
    public static class ProjectMemberInfo {

        private Long id;
        private boolean isLeader;
        private String teamRole;
        private String profileImage;
        private Long userId;
        private String name;
    }
}
