package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.common.dto.IndexRequest;
import backend.techeerzip.domain.common.dto.SlackRequest;
import lombok.Builder;

@Builder
public record ProjectTeamUpdateResponse(
        Long id, SlackRequest slackRequest, IndexRequest indexRequest) {}
