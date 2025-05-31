package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.resume.dto.request.CreateResumeRequest;
import backend.techeerzip.domain.userExperience.dto.request.CreateUserExperienceListRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateUserWithResumeRequest", description = "회원가입 요청 DTO")
public class CreateUserWithResumeRequest {

    @Valid
    @NotNull
    @Schema(description = "사용자 기본 정보")
    private CreateUserRequest createUserRequest;

    @Valid
    @Schema(description = "사용자 경력 정보")
    private CreateUserExperienceListRequest createUserExperienceRequest;

    @Valid
    @Schema(description = "이력서 정보")
    private CreateResumeRequest createResumeRequest;
}
