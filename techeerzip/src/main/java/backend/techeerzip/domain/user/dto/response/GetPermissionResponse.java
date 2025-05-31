package backend.techeerzip.domain.user.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.global.entity.StatusCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPermissionResponse {
    private Long id;
    private Long userId;
    private String name;
    private Long requestedRoleId;
    private StatusCategory status;
    private LocalDateTime createdAt;
}
