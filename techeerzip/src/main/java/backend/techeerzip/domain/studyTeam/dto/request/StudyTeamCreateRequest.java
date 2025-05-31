package backend.techeerzip.domain.studyTeam.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTeamCreateRequest {

    @NotNull @Valid @JsonUnwrapped private StudyData studyData;

    @NotNull @Valid private List<StudyMemberInfoRequest> studyMember;
}
