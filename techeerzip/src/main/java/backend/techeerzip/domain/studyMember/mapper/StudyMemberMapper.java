package backend.techeerzip.domain.studyMember.mapper;

import backend.techeerzip.domain.studyMember.exception.StudyMemberBadRequestException;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.request.StudyMemberInfoRequest;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.StatusCategory;

public class StudyMemberMapper {
    private static final String DEFAULT_MEMBER_SUMMARY = "스터디 멤버입니다.";
    private StudyMemberMapper() {}

    public static StudyMember toEntity(StudyMemberInfoRequest info, StudyTeam team, User user) {
        return StudyMember.builder()
                .isLeader(info.getIsLeader())
                .summary(DEFAULT_MEMBER_SUMMARY)
                .status(StatusCategory.APPROVED)
                .studyTeam(team)
                .user(user)
                .build();
    }

    public static List<StudyMember> toEntities(
            @NotNull List<StudyMemberInfoRequest> incomingMembersInfo,
            @NotNull StudyTeam team,
            @NotNull Map<Long, User> users) {
        if (incomingMembersInfo == null || team == null || users == null) {
            throw new StudyMemberBadRequestException();
        }
        return incomingMembersInfo.stream()
                .map(
                        info ->
                                StudyMember.builder()
                                        .user(users.get(info.getUserId()))
                                        .studyTeam(team)
                                        .isLeader(info.getIsLeader())
                                        .status(StatusCategory.APPROVED)
                                        .summary(DEFAULT_MEMBER_SUMMARY)
                                        .build())
                .toList();
    }
}
