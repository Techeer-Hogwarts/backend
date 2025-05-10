package backend.techeerzip.domain.projectTeam.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;

public interface ProjectTeamFacadeService {

    ResponseEntity<Long> create(ImageRequest imageRequest, ProjectTeamCreateRequest request);

    ResponseEntity<Long> update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request);
}
