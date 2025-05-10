package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.common.dto.IndexRequest;
import backend.techeerzip.domain.common.dto.SlackRequest;

public record ProjectTeamCreateResponse(
        Long id, SlackRequest slackRequest, IndexRequest indexRequest) {}
