package backend.techeerzip.domain.studyTeam.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyData {

    @NotBlank private String name;

    @NotBlank private String studyExplain;

    @NotBlank private String goal;

    @NotBlank private String rule;

    @NotNull private Integer recruitNum;

    @NotNull private Boolean isRecruited;

    @NotNull private Boolean isFinished;

    @Nullable private String recruitExplain;

    @Nullable private String githubLink;

    @Nullable private String notionLink;
}
