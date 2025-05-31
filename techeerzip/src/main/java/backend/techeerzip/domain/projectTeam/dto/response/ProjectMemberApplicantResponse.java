package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberApplicantResponse {

    private Long id;
    private TeamRole teamRole;
    private String summary;
    private StatusCategory status;

    private Long userId;
    private String name;
    private String profileImage;
    private Integer year;
}
