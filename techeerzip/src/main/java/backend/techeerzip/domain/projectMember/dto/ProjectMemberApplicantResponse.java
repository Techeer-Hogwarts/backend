package backend.techeerzip.domain.projectMember.dto;

import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.global.entity.StatusCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "프로젝트 팀 지원자 응답 정보")
public class ProjectMemberApplicantResponse {

    @Schema(description = "프로젝트 멤버 ID", example = "101")
    private Long id;

    @Schema(description = "지원한 역할 (FRONTEND, BACKEND, FULLSTACK 등)", example = "BACKEND")
    private TeamRole teamRole;

    @Schema(description = "자기소개 요약", example = "백엔드 개발 경험이 있으며 스프링과 JPA에 익숙합니다.")
    private String summary;

    @Schema(description = "지원 상태 (PENDING)", example = "PENDING")
    private StatusCategory status;

    @Schema(description = "사용자 ID", example = "302")
    private Long userId;

    @Schema(description = "지원자 이름", example = "홍길동")
    private String name;

    @Schema(description = "지원자 프로필 이미지 URL", example = "https://techeerzip.s3.ap-northeast-2.amazonaws.com/profile/user302.png")
    private String profileImage;

    @Schema(description = "학번/기수 등 지원자의 학년 정보", example = "3")
    private Integer year;
}
