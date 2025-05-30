package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.ProjectIndexRequest;
import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest;

public record ProjectTeamCreateResponse(
        Long id, SlackRequest.Channel slackRequest, ProjectIndexRequest indexRequest) {}
