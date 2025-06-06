package backend.techeerzip.domain.resume.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateResumeRequest", description = "이력서 생성 DTO")
public class CreateResumeRequest {

    @JsonIgnore
    @Schema(hidden = true)
    @Nullable
    private String url;

    @NotNull
    @Schema(description = "이력서 타입", example = "PORTFOLIO")
    private String category;

    @NotBlank
    @Schema(description = "이력서 포지션", example = "BACKEND")
    private String position;

    @Schema(description = "이력서 제목에 추가할 부가 설명", example = "스타트업")
    private String title;

    @NotNull
    @Schema(description = "이력서 대표 지정 여부", example = "true")
    private Boolean isMain;
}
