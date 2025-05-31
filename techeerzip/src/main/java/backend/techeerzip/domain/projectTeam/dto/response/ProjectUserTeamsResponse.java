package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

public record ProjectUserTeamsResponse(Long id, String name, List<String> mainImage) {}
