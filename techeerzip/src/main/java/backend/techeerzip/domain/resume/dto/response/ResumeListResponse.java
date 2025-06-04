package backend.techeerzip.domain.resume.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class ResumeListResponse {
    @Schema(description = "이력서 목록")
    private final List<ResumeResponse> data;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    public ResumeListResponse(List<ResumeResponse> resumes, Integer limit) {
        int safeLimit = (limit != null && limit > 0) ? limit : 10;
        boolean hasNext = resumes.size() > safeLimit;

        if (hasNext) {
            this.hasNext = true;
            this.nextCursor = resumes.get(safeLimit - 1).getId();
            this.data = resumes.subList(0, safeLimit);
        } else {
            this.hasNext = false;
            this.nextCursor = null;
            this.data = resumes;
        }
    }
}
