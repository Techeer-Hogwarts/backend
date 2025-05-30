package backend.techeerzip.domain.projectMember.service;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberDslRepository;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberDslRepository projectMemberDslRepository;
    private final UserRepository userRepository;

    public void checkActive(Long projectTeamId, Long userId) {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        userId, projectTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            throw new ProjectInvalidActiveRequester();
        }
    }

    public boolean isActive(Long projectTeamId, Long userId) {
        return projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                userId, projectTeamId, StatusCategory.APPROVED);
    }

    @Transactional
    public ProjectMember applyApplicant(
            ProjectTeam team, Long applicantId, TeamRole teamRole, String summary) {
        final ProjectMember pm =
                projectMemberRepository
                        .findByProjectTeamIdAndUserId(team.getId(), applicantId)
                        .orElse(createApplicant(team, applicantId, teamRole, summary));
        if (pm.isPending()) {
            pm.toApplicant();
        }
        return pm;
    }

    private ProjectMember createApplicant(
            ProjectTeam team, Long applicantId, TeamRole teamRole, String summary) {
        final ProjectMember pm =
                ProjectMember.builder()
                        .status(StatusCategory.PENDING)
                        .teamRole(teamRole)
                        .summary(summary)
                        .isLeader(false)
                        .projectTeam(team)
                        .user(userRepository.getReferenceById(applicantId))
                        .build();
        return projectMemberRepository.save(pm);
    }

    public List<ProjectMemberApplicantResponse> getApplicants(Long teamId) {
        return projectMemberDslRepository.findManyApplicants(teamId);
    }

    @Transactional
    public String acceptApplicant(Long teamId, Long applicantId) {
        final ProjectMember projectMember =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(ProjectMemberNotFoundException::new);
        if (!projectMember.isActive()) {
            projectMember.toActive();
        }
        return projectMember.getUser().getEmail();
    }

    @Transactional
    public String rejectApplicant(Long teamId, Long applicantId) {
        final ProjectMember projectMember =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(ProjectMemberNotFoundException::new);
        if (!projectMember.isRejected()) {
            projectMember.toReject();
        }
        return projectMember.getUser().getEmail();
    }

    public List<LeaderInfo> getLeaders(Long teamId) {
        return projectMemberDslRepository.findManyLeaders(teamId);
    }
}
