package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SliceNextInfo {

    private final Boolean hasNext;
    private final UUID globalId;
    private final LocalDateTime createdAt;

    @Builder
    public SliceNextInfo(Boolean hasNext, UUID globalId, LocalDateTime createdAt) {
        this.hasNext = hasNext;
        this.globalId = globalId;
        this.createdAt = createdAt;
    }
}
