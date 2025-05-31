package backend.techeerzip.domain.studyTeam.mapper;

import java.util.ArrayList;
import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.global.entity.StatusCategory;

public class StudySlackMapper {
    private static final String NO_APPLICANT = "Null";

    private StudySlackMapper() {}

    public static StudySlackRequest.Channel toChannelRequest(
            StudyTeam team, List<LeaderInfo> leaders) {
        final List<String> leaderNames = leaders.stream().map(LeaderInfo::name).toList();

        final List<String> leaderEmails = leaders.stream().map(LeaderInfo::email).toList();

        return StudySlackRequest.Channel.builder()
                .leader(leaderNames)
                .email(leaderEmails)
                .recruitNum(team.getRecruitNum())
                .id(team.getId())
                .rule(team.getRule())
                .goal(team.getGoal())
                .githubLink(team.getGithubLink())
                .notionLink(team.getNotionLink())
                .recruitExplain(team.getRecruitExplain())
                .studyExplain(team.getStudyExplain())
                .name(team.getName())
                .type(TeamType.STUDY)
                .build();
    }

    public static List<StudySlackRequest.DM> toDmRequest(
            StudyTeam team,
            List<LeaderInfo> leaders,
            String applicantEmail,
            StatusCategory status) {
        final List<StudySlackRequest.DM> alerts = new ArrayList<>();

        for (int i = 0; i < leaders.size(); i++) {
            final String leaderEmail = leaders.get(i).email();
            final String first = (i == 0) ? applicantEmail : NO_APPLICANT;

            alerts.add(
                    new StudySlackRequest.DM(
                            team.getId(),
                            TeamType.STUDY,
                            team.getName(),
                            leaderEmail,
                            first,
                            status));
        }

        return alerts;
    }
}
