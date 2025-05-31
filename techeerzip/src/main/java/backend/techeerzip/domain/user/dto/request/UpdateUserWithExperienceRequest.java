package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.techeerzip.domain.userExperience.dto.request.UpdateUserExperienceListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UpdateUserWithExperienceRequest", description = "사용자 업데이트 DTO")
public class UpdateUserWithExperienceRequest {

    @Valid
    @Schema(description = "사용자 정보")
    @JsonProperty("updateRequest")
    private UpdateUserInfoRequest updateUserInfoRequest;

    @Valid
    @Schema(description = "사용자 경력 정보")
    @JsonProperty("experienceRequest")
    private UpdateUserExperienceListRequest updateUserExperienceRequest;
}
