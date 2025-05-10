package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectTeamCreateWithFilesRequest {

    private final List<MultipartFile> files;
    private final String createProjectTeamRequest;

    @Builder
    private ProjectTeamCreateWithFilesRequest(
            List<MultipartFile> files, String createProjectTeamRequest) {
        this.files = files;
        this.createProjectTeamRequest = createProjectTeamRequest;
    }
}
