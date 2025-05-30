package backend.techeerzip.domain.userExperience.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateUserExperienceListRequest", description = "경력 목록 요청 DTO")
public class CreateUserExperienceListRequest {

    @Valid
    @NotEmpty
    @Schema(description = "경력 정보 배열", required = true)
    private List<CreateUserExperienceRequest> experiences;
}
