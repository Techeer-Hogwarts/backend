package backend.techeerzip.domain.studyTeam.dto.request;

public record StudyAddMembersRequest(Long studyTeamId, Long studyMemberId, Boolean isLeader) {}
