package backend.techeerzip.domain.studyTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;

public record StudyTeamCreateResponse(
        Long id, StudySlackRequest.Channel slackRequest, IndexRequest.Study indexRequest) {}
