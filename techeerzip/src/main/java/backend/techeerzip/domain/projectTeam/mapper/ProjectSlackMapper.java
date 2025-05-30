package backend.techeerzip.domain.projectTeam.mapper;

import java.util.ArrayList;
import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.global.entity.StatusCategory;

public class ProjectSlackMapper {
    private static final String NO_APPLICANT = "Null";

    private ProjectSlackMapper() {}

    public static SlackRequest.Channel toChannelRequest(
            ProjectTeam team, List<LeaderInfo> leaders) {
        final List<String> leaderNames = leaders.stream().map(LeaderInfo::name).toList();

        final List<String> leaderEmails = leaders.stream().map(LeaderInfo::email).toList();

        final List<String> stackNames =
                team.getTeamStacks().stream()
                        .map(teamStack -> teamStack.getStack().getName())
                        .toList();

        return new SlackRequest.Channel(
                team.getId(),
                TeamType.PROJECT,
                team.getName(),
                team.getProjectExplain(),
                team.getFrontendNum(),
                team.getBackendNum(),
                team.getDataEngineerNum(),
                team.getDevopsNum(),
                team.getFullStackNum(),
                leaderNames,
                leaderEmails,
                team.getRecruitExplain(),
                team.getGithubLink(),
                team.getNotionLink(),
                stackNames);
    }

    public static List<SlackRequest.DM> toDmRequest(
            ProjectTeam team,
            List<LeaderInfo> leaders,
            String applicantEmail,
            StatusCategory status) {
        final List<SlackRequest.DM> alerts = new ArrayList<>();

        for (int i = 0; i < leaders.size(); i++) {
            final String leaderEmail = leaders.get(i).email();
            final String first = (i == 0) ? applicantEmail : NO_APPLICANT;

            alerts.add(
                    new SlackRequest.DM(
                            team.getId(),
                            TeamType.PROJECT,
                            team.getName(),
                            leaderEmail,
                            first,
                            status));
        }

        return alerts;
    }
}
