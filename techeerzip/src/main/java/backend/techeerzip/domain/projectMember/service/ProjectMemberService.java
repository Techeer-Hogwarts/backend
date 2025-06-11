package backend.techeerzip.domain.projectMember.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.exception.TeamInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberDslRepository;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberDslRepository projectMemberDslRepository;
    private final UserRepository userRepository;

    public void checkActive(Long projectTeamId, Long userId) {
        log.info(
                "ProjectMember checkActive: 활동 멤버 여부 확인 시작 - teamId={}, userId={}",
                projectTeamId,
                userId);

        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        userId, projectTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            log.info(
                    "ProjectMember checkActive: 활동 멤버 아님 - teamId={}, userId={}",
                    projectTeamId,
                    userId);
            throw new TeamInvalidActiveRequester();
        }
        log.info(
                "ProjectMember checkActive: 활동 멤버 확인 완료 - teamId={}, userId={}",
                projectTeamId,
                userId);
    }

    public boolean isActive(Long projectTeamId, Long userId) {
        log.info("ProjectMember isActive: 활동 여부 조회 - teamId={}, userId={}", projectTeamId, userId);

        return projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                userId, projectTeamId, StatusCategory.APPROVED);
    }

    @Transactional
    public ProjectMember applyApplicant(
            ProjectTeam team, Long applicantId, TeamRole teamRole, String summary) {
        log.info(
                "ProjectMember applyApplicant: 지원자 신청 처리 시작 - teamId={}, userId={}",
                team.getId(),
                applicantId);

        final ProjectMember pm =
                projectMemberRepository
                        .findByProjectTeamIdAndUserId(team.getId(), applicantId)
                        .orElse(createApplicant(team, applicantId, teamRole, summary));
        if (pm.isPending()) {
            pm.toApplicant();
            log.info(
                    "ProjectMember applyApplicant: 기존 지원자 상태 변경됨 - teamId={}, userId={}",
                    team.getId(),
                    applicantId);
        }
        return pm;
    }

    private ProjectMember createApplicant(
            ProjectTeam team, Long applicantId, TeamRole teamRole, String summary) {
        log.info(
                "ProjectMember createApplicant: 신규 지원자 생성 - teamId={}, userId={}, teamRole={}",
                team.getId(),
                applicantId,
                teamRole);

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
        log.info("ProjectMember getApplicants: 지원자 목록 조회 시작 - teamId={}", teamId);
        final List<ProjectMemberApplicantResponse> applicants =
                projectMemberDslRepository.findManyApplicants(teamId);
        log.info(
                "ProjectMember getApplicants: 지원자 수 - teamId={}, count={}",
                teamId,
                applicants.size());
        return applicants;
    }

    @Transactional
    public String acceptApplicant(Long teamId, Long applicantId) {
        log.info(
                "ProjectMember acceptApplicant: 지원자 승인 시작 - teamId={}, userId={}",
                teamId,
                applicantId);
        final ProjectMember projectMember =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(
                                () -> {
                                    log.error(
                                            "ProjectMember acceptApplicant: 지원자 조회 오류 - teamId={}, userId={}",
                                            teamId,
                                            applicantId);
                                    return new ProjectMemberNotFoundException();
                                });

        if (!projectMember.isActive()) {
            projectMember.toActive();
            log.info(
                    "ProjectMember acceptApplicant: 상태를 APPROVED로 변경, userEmail={}",
                    projectMember.getUser().getEmail());
        }
        return projectMember.getUser().getEmail();
    }

    @Transactional
    public String rejectApplicant(Long teamId, Long applicantId) {
        log.info(
                "ProjectMember rejectApplicant: 지원자 거절 시작 - teamId={}, userId={}",
                teamId,
                applicantId);

        final ProjectMember projectMember =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(
                                () -> {
                                    log.error(
                                            "ProjectMember rejectApplicant: 지원자 조회 오류 - teamId={}, userId={}",
                                            teamId,
                                            applicantId);
                                    return new ProjectMemberNotFoundException();
                                });

        if (!projectMember.isRejected()) {
            projectMember.toReject();
            log.info(
                    "ProjectMember rejectApplicant: 지원자 상태 → 거절됨 - userEmail={}",
                    projectMember.getUser().getEmail());
        }
        return projectMember.getUser().getEmail();
    }

    public List<LeaderInfo> getLeaders(Long teamId) {
        log.info("ProjectMember getLeaders: 리더 목록 조회 시작 - teamId={}", teamId);
        final List<LeaderInfo> leaders = projectMemberDslRepository.findManyLeaders(teamId);
        log.info("ProjectMember getLeaders: 리더 수 - teamId={}, count={}", teamId, leaders.size());
        return leaders;
    }

    public Optional<ProjectMember> getMember(Long teamId, Long userId) {
        return projectMemberRepository.findByProjectTeamIdAndUserId(teamId, userId);
    }
}
