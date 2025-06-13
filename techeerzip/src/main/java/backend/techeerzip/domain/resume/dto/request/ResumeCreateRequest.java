package backend.techeerzip.domain.resume.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResumeCreateRequest {
    @NotBlank(message = "카테고리는 필수 입력값입니다.")
    private String category;

    @NotBlank(message = "포지션은 필수 입력값입니다.")
    private String position;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotNull(message = "메인 여부는 필수 입력값입니다.")
    private Boolean isMain;
}
