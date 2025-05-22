package backend.techeerzip.domain.studyMember;

import java.util.List;
import java.util.Map;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.request.StudyMemberInfoRequest;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.StatusCategory;

public class StudyMemberMapper {
    private StudyMemberMapper() {}

    public static StudyMember toEntity(StudyMemberInfoRequest info, StudyTeam team, User user) {
        return StudyMember.builder()
                .isLeader(info.getIsLeader())
                .summary("스터디 멤버입니다.")
                .status(StatusCategory.APPROVED)
                .studyTeam(team)
                .user(user)
                .build();
    }

    public static List<StudyMember> toEntities(
            List<StudyMemberInfoRequest> incomingMembersInfo,
            StudyTeam team,
            Map<Long, User> users) {
        return incomingMembersInfo.stream()
                .map(
                        info ->
                                StudyMember.builder()
                                        .user(users.get(info.getUserId()))
                                        .studyTeam(team)
                                        .isLeader(info.getIsLeader())
                                        .status(StatusCategory.APPROVED)
                                        .summary("스터디 멤버입니다.")
                                        .build())
                .toList();
    }
}
