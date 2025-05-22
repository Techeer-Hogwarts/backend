package backend.techeerzip.domain.studyTeam.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTeamDetailResponse {

    private Long id;
    private String name;
    private String notionLink;
    private String githubLink;

    private String recruitExplain;
    private int recruitNum;
    private String rule;
    private String goal;
    private String studyExplain;

    private boolean isRecruited;
    private boolean isFinished;

    private List<ResultImageInfo> resultImages;
    private List<StudyMemberInfo> studyMember;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultImageInfo {
        private Long id;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyMemberInfo {
        private Long id;
        private String name;
        private boolean isLeader;
        private String profileImage;
        private Long userId;
    }
}
