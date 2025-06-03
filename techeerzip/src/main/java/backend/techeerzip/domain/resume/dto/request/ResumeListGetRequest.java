package backend.techeerzip.domain.resume.dto.request;

import backend.techeerzip.domain.resume.entity.ResumeCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeListGetRequest {
    @Schema(
        description = "검색할 포지션 (여러 개 가능) - BACKEND, FRONTEND, DEVOPS, FULL_STACK, DATA_ENGINEER",
        example = "[\"BACKEND\", \"FRONTEND\"]",
        allowableValues = {"BACKEND", "FRONTEND", "DEVOPS", "FULL_STACK", "DATA_ENGINEER"}
    )
    private List<String> position;

    @Schema(
        description = "검색할 기수 (여러 개 가능)",
        example = "[1, 2, 3]"
    )
    private List<Integer> year;

    @Schema(
        description = "카테고리 - 전체/RESUME/PORTFOLIO/ICT/OTHER (기본값: 전체)",
        example = "OTHER",
        allowableValues = {"RESUME", "PORTFOLIO", "ICT", "OTHER"}
    )
    private String category;

    @Schema(
        description = "마지막으로 조회한 이력서의 ID",
        example = "0"
    )
    @Min(value = 0, message = "cursorId는 0 이상의 정수여야 합니다.")
    private Long cursorId;

    @Schema(
        description = "가져올 개수",
        example = "10"
    )
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit;
}
