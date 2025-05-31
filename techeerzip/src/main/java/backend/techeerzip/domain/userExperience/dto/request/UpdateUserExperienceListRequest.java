package backend.techeerzip.domain.userExperience.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UpdateUserExperienceListRequest", description = "경력 수정 리스트 DTO")
public class UpdateUserExperienceListRequest {

    @Schema(description = "경력 리스트")
    private List<UpdateUserExperienceRequest> experiences;
}
