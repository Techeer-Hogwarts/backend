package backend.techeerzip.domain.projectMember.mapper;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.StatusCategory;

public class ProjectMemberMapper {

    private ProjectMemberMapper() {}

    public static ProjectMember toEntity(
            ProjectMemberInfoRequest member,
            ProjectTeam projectTeam,
            User user,
            @Nullable String summary) {
        return ProjectMember.builder()
                .teamRole(member.teamRole())
                .summary(summary)
                .isLeader(member.isLeader())
                .status(StatusCategory.APPROVED)
                .projectTeam(projectTeam)
                .user(user)
                .build();
    }

    public static List<ProjectMember> toEntities(
            List<ProjectMemberInfoRequest> incomingMembersInfo,
            ProjectTeam currentTeam,
            Map<Long, User> users) {
        return incomingMembersInfo.stream()
                .map(
                        m -> {
                            User user = users.get(m.userId());
                            if (user == null) {
                                throw new IllegalArgumentException(
                                        "User not found with ID: " + m.userId());
                            }
                            return ProjectMemberMapper.toEntity(m, currentTeam, user, "신규 회원 입니다.");
                        })
                .toList();
    }
}
