package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitCounts {

    @NotNull
    @Min(0)
    private Integer frontedNum;

    @NotNull
    @Min(0)
    private Integer backendNum;

    @NotNull
    @Min(0)
    private Integer fullStackNum;

    @NotNull
    @Min(0)
    private Integer devOpsNum;

    @NotNull
    @Min(0)
    private Integer dataEngineerNum;
}
