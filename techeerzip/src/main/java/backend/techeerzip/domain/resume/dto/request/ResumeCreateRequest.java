package backend.techeerzip.domain.resume.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResumeCreateRequest {
    private String category;
    private String position;
    private String title;
    private Boolean isMain;
}
