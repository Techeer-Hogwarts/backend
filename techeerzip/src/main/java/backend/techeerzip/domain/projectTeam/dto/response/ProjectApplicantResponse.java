package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.common.dto.IndexRequest;
import backend.techeerzip.domain.common.dto.SlackRequest;

public record ProjectApplicantResponse(SlackRequest slackRequest, IndexRequest indexRequest) {}
