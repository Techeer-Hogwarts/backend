package backend.techeerzip.domain.userExperience.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "CreateUserExperienceListRequest", description = "경력 목록 요청 DTO")
public class CreateUserExperienceListRequest {

    @Valid
    @NotEmpty
    @Schema(description = "경력 정보 배열", required = true)
    private List<CreateUserExperienceRequest> experiences;
}
