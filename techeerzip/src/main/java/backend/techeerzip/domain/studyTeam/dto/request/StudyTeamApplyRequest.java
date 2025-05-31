package backend.techeerzip.domain.studyTeam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StudyTeamApplyRequest(
        @NotNull @Positive Long studyTeamId, @NotBlank String summary) {}
