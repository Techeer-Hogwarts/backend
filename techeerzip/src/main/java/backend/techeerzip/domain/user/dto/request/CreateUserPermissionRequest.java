package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateUserPermissionRequest {
    @NotNull
    @Schema(description = "요청한 권한 ID", example = "2")
    private Long roleId;
}
