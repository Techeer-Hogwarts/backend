package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;

import org.springframework.web.multipart.MultipartFile;

public record ImageRequest(@Nullable MultipartFile mainImage, List<MultipartFile> resultImages) {}
