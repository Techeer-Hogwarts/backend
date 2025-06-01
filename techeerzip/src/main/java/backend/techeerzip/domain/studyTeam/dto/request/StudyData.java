package backend.techeerzip.domain.studyTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디 생성/수정 요청에 포함되는 핵심 정보")
public class StudyData {

    @NotBlank
    @Schema(description = "스터디 이름", example = "CS 스터디")
    private String name;

    @NotBlank
    @Schema(description = "스터디 설명", example = "운영체제와 네트워크를 함께 공부하는 스터디입니다.")
    private String studyExplain;

    @NotBlank
    @Schema(description = "스터디 목표", example = "면접 대비를 위한 컴퓨터공학 이론 학습")
    private String goal;

    @NotBlank
    @Schema(description = "스터디 규칙", example = "매주 일요일까지 정리본 제출, 3회 이상 무단 결석 시 탈퇴")
    private String rule;

    @NotNull
    @Schema(description = "모집 인원 수", example = "5")
    private Integer recruitNum;

    @NotNull
    @Schema(description = "모집 중 여부", example = "true")
    private Boolean isRecruited;

    @NotNull
    @Schema(description = "스터디 종료 여부", example = "false")
    private Boolean isFinished;

    @Nullable
    @Schema(description = "모집 설명 (선택)", example = "같이 열심히 공부하실 분 구합니다!")
    private String recruitExplain;

    @Nullable
    @Schema(description = "스터디 관련 Github 링크", example = "https://github.com/techeerzip/study-repo")
    private String githubLink;

    @Nullable
    @Schema(description = "스터디 관련 Notion 링크", example = "https://techeer.notion.site/study-plan")
    private String notionLink;
}
