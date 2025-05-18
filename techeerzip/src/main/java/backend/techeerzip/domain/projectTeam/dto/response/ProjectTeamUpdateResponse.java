package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.ProjectIndexRequest;
import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest;
import lombok.Builder;

@Builder
public record ProjectTeamUpdateResponse(
        Long id, SlackRequest.Channel slackRequest, ProjectIndexRequest indexRequest) {}
