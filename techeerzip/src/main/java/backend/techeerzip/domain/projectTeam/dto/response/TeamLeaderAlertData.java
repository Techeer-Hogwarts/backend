package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

public record TeamLeaderAlertData(String teamId, String teamName, List<String> leaderEmail) {}
