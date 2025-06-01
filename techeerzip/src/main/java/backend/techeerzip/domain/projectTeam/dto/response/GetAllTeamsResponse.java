package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "전체 팀 목록 응답")
public record GetAllTeamsResponse(

        @Schema(
                description = "조회된 팀 목록 (프로젝트 또는 스터디)",
                oneOf = {ProjectSliceTeamsResponse.class, StudySliceTeamsResponse.class}
        )
        List<SliceTeamsResponse> teams,

        @Schema(description = "다음 페이지 커서 정보", required = true)
        SliceNextCursor nextInfo
) {}
