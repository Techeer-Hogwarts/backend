package backend.techeerzip.domain.studyTeam.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMemberInfoRequest {

    @NotNull private Long userId;

    @NotNull private Boolean isLeader;
}
