package backend.techeerzip.domain.studyTeam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디 팀 상세 조회 응답")
public class StudyTeamDetailResponse {

    @Schema(description = "스터디 팀 ID", example = "1")
    private Long id;

    @Schema(description = "스터디 이름", example = "알고리즘 스터디")
    private String name;

    @Schema(description = "노션 링크", example = "https://notion.so/study123")
    private String notionLink;

    @Schema(description = "GitHub 링크", example = "https://github.com/studyteam")
    private String githubLink;

    @Schema(description = "모집 설명", example = "함께 성장할 백엔드/프론트엔드 멤버를 모집합니다.")
    private String recruitExplain;

    @Schema(description = "총 모집 인원", example = "5")
    private int recruitNum;

    @Schema(description = "스터디 규칙", example = "매주 3회 오프라인 진행")
    private String rule;

    @Schema(description = "스터디 목표", example = "백준 실버 → 골드 달성")
    private String goal;

    @Schema(description = "스터디 설명", example = "꾸준한 알고리즘 학습을 위한 스터디입니다.")
    private String studyExplain;

    @Schema(description = "모집 중 여부", example = "true")
    private boolean isRecruited;

    @Schema(description = "스터디 종료 여부", example = "false")
    private boolean isFinished;

    @Schema(description = "스터디 결과 이미지 목록")
    private List<ResultImageInfo> resultImages;

    @Schema(description = "스터디 팀원 정보 목록")
    private List<StudyMemberInfo> studyMember;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "스터디 결과 이미지 정보")
    public static class ResultImageInfo {

        @Schema(description = "이미지 ID", example = "10")
        private Long id;

        @Schema(description = "이미지 URL", example = "https://s3.techeerzip.com/result/10.png")
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "스터디 팀원 정보")
    public static class StudyMemberInfo {

        @Schema(description = "스터디 멤버 ID", example = "200")
        private Long id;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "스터디 리더 여부", example = "true")
        private boolean isLeader;

        @Schema(description = "프로필 이미지 URL", example = "https://s3.techeerzip.com/profile/200.png")
        private String profileImage;

        @Schema(description = "사용자 ID", example = "300")
        private Long userId;
    }
}
