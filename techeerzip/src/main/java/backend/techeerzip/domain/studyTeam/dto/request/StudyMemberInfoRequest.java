package backend.techeerzip.domain.studyTeam.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스터디 멤버 정보 요청")
public class StudyMemberInfoRequest {

    @NotNull
    @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull
    @Schema(description = "리더 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isLeader;
}
