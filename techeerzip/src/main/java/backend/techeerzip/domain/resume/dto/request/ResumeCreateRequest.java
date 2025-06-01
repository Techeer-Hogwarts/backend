package backend.techeerzip.domain.resume.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ResumeCreateRequest {

    private String url;
    private String category;
    private String position;
    private String title;
    private Boolean isMain;
}