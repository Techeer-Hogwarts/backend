package backend.techeerzip.domain.studyTeam.dto.request;

import java.util.ArrayList;
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
public class StudyTeamUpdateRequest {

    @NotNull @Valid @JsonUnwrapped private StudyData studyData;

    @NotNull @Valid @Builder.Default
    private List<StudyMemberInfoRequest> studyMember = new ArrayList<>();

    @Builder.Default private List<Long> deleteImages = new ArrayList<>();

    @Builder.Default private List<Long> deleteMembers = new ArrayList<>();
}
