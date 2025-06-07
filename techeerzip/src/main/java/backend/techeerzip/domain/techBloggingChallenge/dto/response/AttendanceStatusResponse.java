package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "AttendanceStatusResponse", description = "챌린지 출석 현황 응답 DTO")
public class AttendanceStatusResponse {
    @Schema(description = "유저 ID", example = "1")
    private final Long userId;

    @Schema(description = "유저 이름", example = "김진희")
    private final String userName;

    @Schema(description = "회차별 제출 블로그 개수", example = "[1, 0, 0, 4, 2, 1, 3, 2, 0, 0, 0, 0]")
    private final List<Integer> sequence;

    @Schema(description = "전체 제출 블로그 수", example = "13")
    private final int totalCount;
}
