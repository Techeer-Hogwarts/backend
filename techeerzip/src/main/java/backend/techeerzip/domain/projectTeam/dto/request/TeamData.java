package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

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
    @Nullable
    @URL(message = "올바른 Github URL 형식이 아닙니다")
    private String githubLink;
    @Nullable
    @URL(message = "올바른 Notion URL 형식이 아닙니다")
    private String notionLink;
}
