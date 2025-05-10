package backend.techeerzip.domain.projectTeam.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.service.ProjectTeamFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projectTeams")
@RequiredArgsConstructor
public class ProjectTeamController {

    private final ProjectTeamFacadeService projectTeamFacadeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProjectTeam(
            @RequestPart("files") List<MultipartFile> images,
            @RequestPart("createProjectTeamRequest") ProjectTeamCreateRequest request) {
        return projectTeamFacadeService.create(ProjectTeamMapper.separateImages(images), request);
    }

    @PatchMapping(value = "/{projectTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateProjectTeam(
            @PathVariable Long projectTeamId,
            @RequestPart(value = "mainImages", required = false) MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateProjectTeamRequest") ProjectTeamUpdateRequest request) {
        final Long userId = 1L;
        return projectTeamFacadeService.update(
                projectTeamId, userId, mainImage, resultImages, request);
    }
}
