package backend.techeerzip.domain.studyTeam.dto.response;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;
import lombok.Builder;

@Builder
public record StudyTeamUpdateResponse(
        Long id, StudySlackRequest.Channel slackRequest, IndexRequest.Study indexRequest) {}
