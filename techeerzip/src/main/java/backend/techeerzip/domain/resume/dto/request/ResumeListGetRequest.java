package backend.techeerzip.domain.resume.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeListGetRequest {
    @Schema(
        description = "검색할 직책 (여러 개 가능) - BACKEND, FRONTEND, DEVOPS, FULL_STACK, DATA_ENGINEER",
        example = "[\"BACKEND\", \"FRONTEND\"]"
    )
    private List<String> position;

    @Schema(
        description = "검색할 기수 (여러 개 가능)",
        example = "[1, 2, 3]"
    )
    private List<Integer> year;

    @Schema(
        description = "카테고리 - 전체/RESUME/PORTFOLIO/ICT/OTHER (기본값: 전체)",
        example = "OTHER"
    )
    private String category;

    @Schema(
        description = "오프셋",
        example = "0"
    )
    private Integer offset;

    @Schema(
        description = "가져올 개수",
        example = "10"
    )
    private Integer limit;
}
