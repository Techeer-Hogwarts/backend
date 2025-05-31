package backend.techeerzip.domain.studyTeam.dto.response;

import backend.techeerzip.global.entity.StatusCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyApplicantResponse {

    private Long id;
    private String summary;
    private StatusCategory status;

    private Long userId;
    private String name;
    private String profileImage;
    private Integer year;
}
