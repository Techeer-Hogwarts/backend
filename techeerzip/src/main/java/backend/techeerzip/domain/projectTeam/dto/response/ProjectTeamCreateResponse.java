package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest;

public record ProjectTeamCreateResponse(
        Long id, ProjectSlackRequest.Channel slackRequest, IndexRequest.Project indexRequest) {}
