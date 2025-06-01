package backend.techeerzip.domain.projectTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamData {

    @NotBlank
    @Schema(description = "팀 이름", example = "하루Zip")
    private String name;

    @NotBlank
    @Schema(description = "프로젝트 설명", example = "AI 이미지 생성 서비스")
    private String projectExplain;

    @NotNull
    @Schema(description = "모집 여부", example = "true")
    private Boolean isRecruited;

    @NotNull
    @Schema(description = "진행 완료 여부", example = "false")
    private Boolean isFinished;

    @Nullable
    @Schema(description = "모집 설명", example = "프론트엔드 1명, 백엔드 1명 모집 중")
    private String recruitExplain;

    @Nullable
    @URL(message = "올바른 Github URL 형식이 아닙니다")
    @Schema(description = "Github 링크", example = "https://github.com/techeerzip")
    private String githubLink;

    @Nullable
    @URL(message = "올바른 Notion URL 형식이 아닙니다")
    @Schema(description = "Notion 링크", example = "https://www.notion.so/techeerzip")
    private String notionLink;
}
