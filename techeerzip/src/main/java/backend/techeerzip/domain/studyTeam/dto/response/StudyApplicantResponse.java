package backend.techeerzip.domain.studyTeam.dto.response;

import backend.techeerzip.global.entity.StatusCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "스터디 지원자 응답 DTO")
public class StudyApplicantResponse {

    @Schema(description = "스터디 지원 ID", example = "12")
    private Long id;

    @Schema(description = "지원자가 작성한 요약 소개", example = "백엔드 개발자로서 협업 경험을 쌓고 싶습니다.")
    private String summary;

    @Schema(description = "지원 상태", example = "PENDING")
    private StatusCategory status;

    @Schema(description = "사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "지원자 이름", example = "홍길동")
    private String name;

    @Schema(
            description = "지원자 프로필 이미지 URL",
            example =
                    "https://techeerzip-bucket.s3.ap-southeast-2.amazonaws.com/profiles/user-12345.jpg")
    private String profileImage;

    @Schema(description = "지원자의 학번 (입학 연도)", example = "21")
    private Integer year;
}
