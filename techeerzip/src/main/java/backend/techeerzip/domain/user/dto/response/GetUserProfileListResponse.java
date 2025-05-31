package backend.techeerzip.domain.user.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "GetUserProfileListResponse", description = "유저 프로필 목록 응답 DTO")
public class GetUserProfileListResponse {

    @Schema(description = "유저 프로필 목록")
    private final List<GetUserResponse> profiles;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;
}
