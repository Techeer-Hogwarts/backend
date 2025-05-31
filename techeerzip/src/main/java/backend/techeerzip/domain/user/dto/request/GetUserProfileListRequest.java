package backend.techeerzip.domain.user.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GetUserProfileListRequest", description = "유저 전체 프로필 요청 DTO")
public class GetUserProfileListRequest {
    @Schema(description = "직무 (mainPosition)", example = "[\"BACKEND\", \"FRONTEND\"]")
    private List<String> position;

    @Schema(description = "테커 기수", example = "[6, 7, 8]")
    private List<Integer> year;

    @Schema(description = "학교명", example = "[\"인천대학교\"]")
    private List<String> university;

    @Schema(description = "학년", example = "[\"4학년\", \"졸업\"]")
    private List<String> grade;

    @Schema(description = "커서 ID", example = "10")
    private Long cursorId;

    @Schema(description = "조회 제한 수", example = "10", defaultValue = "10")
    private Integer limit;

    @Schema(description = "정렬 기준", example = "year")
    private String sortBy;
}
