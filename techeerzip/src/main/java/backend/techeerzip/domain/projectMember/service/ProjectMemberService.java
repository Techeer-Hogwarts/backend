package backend.techeerzip.domain.projectMember.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberDslRepository;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
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
    private final ProjectTeamRepository projectTeamRepository;

    public boolean checkActiveMemberByTeamAndUser(Long projectTeamId, Long userId) {
        return projectMemberRepository.existsByProjectTeamIdAndUserId(projectTeamId, userId);
    }

    @Transactional
    public ProjectMember applyApplicant(
            ProjectTeam team, Long applicantId, TeamRole teamRole, String summary) {
        final ProjectMember pm =
                projectMemberRepository
                        .findByProjectTeamIdAndUserId(team.getId(), applicantId)
                        .orElse(createApplicant(team, applicantId, teamRole, summary));
        pm.toApplicant();
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
        projectMember.toActive();
        return projectMember.getUser().getEmail();
    }

    @Transactional
    public String rejectApplicant(Long teamId, Long applicantId) {
        final ProjectMember projectMember =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(ProjectMemberNotFoundException::new);
        projectMember.toReject();
        return projectMember.getUser().getEmail();
    }

    public List<LeaderInfo> getLeaders(Long teamId) {
        return projectMemberDslRepository.findManyLeaders(teamId);
    }
}
