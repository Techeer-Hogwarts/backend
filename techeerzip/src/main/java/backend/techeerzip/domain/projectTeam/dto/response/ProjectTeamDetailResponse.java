package backend.techeerzip.domain.projectTeam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@Schema(description = "프로젝트 팀 상세 정보 응답")
public class ProjectTeamDetailResponse {

    @Schema(description = "프로젝트 팀 ID", example = "1")
    private Long id;

    @Schema(description = "삭제 여부", example = "false")
    private boolean isDeleted;

    @Schema(description = "모집 중 여부", example = "true")
    private boolean isRecruited;

    @Schema(description = "모집 완료 여부", example = "false")
    private boolean isFinished;

    @Schema(description = "팀 이름", example = "테크짓 프로젝트팀")
    private String name;

    @Schema(description = "GitHub 링크", example = "https://github.com/techeerzip/project")
    private String githubLink;

    @Schema(description = "Notion 링크", example = "https://www.notion.so/project-doc")
    private String notionLink;

    @Schema(description = "프로젝트 설명", example = "이 프로젝트는 커뮤니티 플랫폼을 개발하는 프로젝트입니다.")
    private String projectExplain;

    @Schema(description = "프론트엔드 모집 인원", example = "2")
    private int frontendNum;

    @Schema(description = "백엔드 모집 인원", example = "3")
    private int backendNum;

    @Schema(description = "DevOps 모집 인원", example = "1")
    private int devopsNum;

    @Schema(description = "풀스택 모집 인원", example = "0")
    private int fullStackNum;

    @Schema(description = "데이터 엔지니어 모집 인원", example = "1")
    private int dataEngineerNum;

    @Schema(description = "모집 설명", example = "활발하게 소통하며 같이 성장할 팀원을 찾습니다.")
    private String recruitExplain;

    @Schema(description = "결과 이미지 목록")
    private List<ResultImageInfo> resultImages;

    @Schema(description = "메인 이미지 목록")
    private List<MainImageInfo> mainImages;

    @Schema(description = "팀 스택 정보 목록")
    private List<TeamStackDetail> teamStacks;

    @Schema(description = "프로젝트 팀원 목록")
    private List<ProjectMemberInfo> projectMember;

    // =================== 내부 정적 클래스 ===================

    @Getter
    @Builder
    @Schema(description = "프로젝트 결과 이미지 정보")
    public static class ResultImageInfo {

        @Schema(description = "이미지 ID", example = "10")
        private Long id;

        @Schema(description = "삭제 여부", example = "false")
        private boolean isDeleted;

        @Schema(description = "이미지 URL", example = "https://techeerzip.s3.amazonaws.com/result1.png")
        private String imageUrl;
    }

    @Getter
    @Builder
    @Schema(description = "프로젝트 메인 이미지 정보")
    public static class MainImageInfo {

        @Schema(description = "이미지 ID", example = "5")
        private Long id;

        @Schema(description = "삭제 여부", example = "false")
        private boolean isDeleted;

        @Schema(description = "이미지 URL", example = "https://techeerzip.s3.amazonaws.com/main1.png")
        private String imageUrl;
    }

    @Getter
    @Builder
    @Schema(description = "팀 스택 정보")
    public static class TeamStackDetail {

        @Schema(description = "팀 스택 ID", example = "22")
        private Long id;

        @Schema(description = "삭제 여부", example = "false")
        private boolean isDeleted;

        @Schema(description = "프로젝트 팀 ID", example = "1")
        private Long projectTeamId;

        @Schema(description = "주 스택 여부", example = "true")
        private boolean isMain;

        @Schema(description = "스택 정보")
        private StackInfo stack;

        @Getter
        @Builder
        @Schema(description = "스택 정보")
        public static class StackInfo {

            @Schema(description = "스택 이름", example = "Spring Boot")
            private String name;

            @Schema(description = "스택 카테고리", example = "BACKEND")
            private String category;
        }
    }

    @Getter
    @Builder
    @Schema(description = "프로젝트 팀원 정보")
    public static class ProjectMemberInfo {

        @Schema(description = "팀원 ID", example = "99")
        private Long id;

        @Schema(description = "리더 여부", example = "true")
        private boolean isLeader;

        @Schema(description = "팀 역할", example = "BACKEND")
        private String teamRole;

        @Schema(description = "프로필 이미지 URL", example = "https://techeerzip.s3.amazonaws.com/user123.png")
        private String profileImage;

        @Schema(description = "사용자 ID", example = "123")
        private Long userId;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;
    }
}
