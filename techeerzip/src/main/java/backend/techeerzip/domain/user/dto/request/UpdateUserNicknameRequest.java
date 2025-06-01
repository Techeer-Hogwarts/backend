package backend.techeerzip.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "UpdateUserNicknameRequest", description = "닉네임 업데이트" + " 요청 DTO")
public class UpdateUserNicknameRequest {

    @NotBlank
    @Schema(description = "닉네임", example = "테커123")
    private String nickname;
}
