package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateUserPermissionRequest {
    @NotNull
    @Schema(description = "승인할 사용자 ID", example = "1")
    private Long userId;

    @NotNull
    @Schema(description = "부여할 권한 ID", example = "2")
    private Long newRoleId;
}
