package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamData {

    @NotBlank private String name;
    @NotBlank private String projectExplain;
    @NotNull private Boolean isRecruited;
    @NotNull private Boolean isFinished;
    @Nullable private String recruitExplain;
    @Nullable private String githubLink;
    @Nullable private String notionLink;
}
