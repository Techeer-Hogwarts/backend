package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest;
import lombok.Builder;

@Builder
public record ProjectTeamUpdateResponse(
        Long id, ProjectSlackRequest.Channel slackRequest, IndexRequest.Project indexRequest) {}
