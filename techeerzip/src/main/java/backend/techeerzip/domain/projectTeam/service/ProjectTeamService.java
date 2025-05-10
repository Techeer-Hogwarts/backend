package backend.techeerzip.domain.projectTeam.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.common.dto.IndexRequest;
import backend.techeerzip.domain.common.dto.SlackRequest;
import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.projectTeam.exception.ProjectDuplicateTeamName;
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidProjectMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidTeamStackException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingLeaderException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamNotFoundException;
import backend.techeerzip.domain.projectTeam.repository.ProjectMainImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectResultImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamStackRepository;
import backend.techeerzip.domain.projectTeam.repository.querydsl.ProjectTeamDslRepository;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.repository.StackRepository;
import backend.techeerzip.domain.studyTeam.service.StudyTeamService;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTeamService {

    private final TeamStackService teamStackService;
    private final StudyTeamService studyTeamService;
    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamDslRepository projectTeamDslRepository;
    private final S3Service s3Service;
    private final TeamStackService projectTeamWriter;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectMainImageRepository mainImgRepository;
    private final ProjectResultImageRepository resultImgRepository;
    private final ProjectTeamStackRepository teamStackRepository;
    private final StackRepository stackRepository;
    private final CustomLogger log;

    private static void checkValidProjectMembers(
            List<ProjectMemberInfoRequest> membersInfo, Map<Long, User> users) {
        final Set<Long> notFound =
                membersInfo.stream()
                        .map(ProjectMemberInfoRequest::userId)
                        .filter(id -> !users.containsKey(id))
                        .collect(Collectors.toSet());

        if (!notFound.isEmpty()) {
            throw new ProjectInvalidProjectMemberException();
        }
    }

    private static void validateTeamStacks(
            List<Stack> stacks, Map<String, Boolean> teamStackNameAndIsMainStatus) {
        if (stacks.size() != teamStackNameAndIsMainStatus.size()) {
            throw new ProjectInvalidTeamStackException();
        }
    }

    private static void validateLeaderExists(List<ProjectMemberInfoRequest> membersInfo) {
        if (membersInfo.stream().noneMatch(ProjectMemberInfoRequest::isLeader)) {
            throw new ProjectTeamMissingLeaderException();
        }
    }

    private static void validateProjectActiveMember(Long userId, ProjectTeam team) {
        if (!team.hasUserId(userId)) {
            throw new ProjectInvalidActiveRequester();
        }
    }

    /*
     * 인덱스, 슬랙 설정
     * */
    @Transactional
    public ProjectTeamCreateResponse create(
            List<String> mainImage, List<String> resultImages, ProjectTeamCreateRequest request) {
        this.log.debug("CreateProjectTeam: 시작");
        final TeamData teamData = request.getTeamData();
        final RecruitCounts recruitCounts = request.getRecruitCounts();
        final List<ProjectMemberInfoRequest> membersInfo = request.getProjectMember();
        final List<TeamStackInfo.WithName> teamStacksInfo = request.getTeamStacks();
        // 1. validate
        final boolean isRecruited = checkRecruit(recruitCounts, teamData.getIsRecruited());
        checkUniqueProjectName(teamData.getName());
        this.log.debug("CreateProjectTeam: 이름 중복 검증 완료");
        List<TeamStackInfo.WithStack> teamStacks = teamStackService.create(teamStacksInfo);
        this.log.debug("CreateProjectTeam: 팀 스택 검증 완료");

        final Map<Long, User> users = getLongUserMap(membersInfo);
        this.log.debug("CreateProjectTeam: 프로젝트 멤버 검증 완료");

        // 3. mapToEntities
        // ProjectTeam
        final ProjectTeam teamEntity =
                ProjectTeam.builder()
                        .name(teamData.getName())
                        .githubLink(teamData.getGithubLink())
                        .notionLink(teamData.getNotionLink())
                        .projectExplain(teamData.getProjectExplain())
                        .recruitExplain(teamData.getRecruitExplain())
                        .isFinished(teamData.getIsFinished())
                        .isRecruited(isRecruited)
                        .backendNum(recruitCounts.getBackendNum())
                        .frontendNum(recruitCounts.getFrontedNum())
                        .fullStackNum(recruitCounts.getFullStackNum())
                        .devopsNum(recruitCounts.getDevOpsNum())
                        .dataEngineerNum(recruitCounts.getDataEngineerNum())
                        .build();
        // ProjectMember
        final List<ProjectMember> memberEntities =
                membersInfo.stream()
                        .map(
                                member ->
                                        ProjectMember.builder()
                                                .teamRole(member.teamRole())
                                                .summary("초기 회원 입니다.")
                                                .isLeader(member.isLeader())
                                                .status(StatusCategory.APPROVED)
                                                .projectTeam(teamEntity)
                                                .user(users.get(member.userId()))
                                                .build())
                        .toList();
        final ProjectMainImage mainImgEntity =
                ProjectMainImage.builder()
                        .imageUrl(mainImage.getFirst())
                        .projectTeam(teamEntity)
                        .build();
        final List<ProjectResultImage> resultImageEntities =
                resultImages.stream()
                        .map(
                                url ->
                                        ProjectResultImage.builder()
                                                .imageUrl(url)
                                                .projectTeam(teamEntity)
                                                .build())
                        .toList();
        final List<TeamStack> teamStackEntities =
                teamStacks.stream()
                        .map(
                                s ->
                                        TeamStack.builder()
                                                .stack(s.getStack())
                                                .isMain(s.getIsMain())
                                                .projectTeam(teamEntity)
                                                .build())
                        .toList();
        teamEntity.addProjectMembers(memberEntities);
        teamEntity.addProjectMainImages(List.of(mainImgEntity));
        teamEntity.addProjectResultImages(resultImageEntities);
        teamEntity.addTeamStacks(teamStackEntities);
        this.log.debug("CreateProjectTeam: 엔티티 맵핑 완료");

        final Long savedTeamId = projectTeamRepository.save(teamEntity).getId();
        this.log.debug("CreateProjectTeam: 프로젝트 팀 생성 완료");

        // indexService, slackService
        return new ProjectTeamCreateResponse(savedTeamId, new SlackRequest(), new IndexRequest());
    }

    private Map<Long, User> getLongUserMap(List<ProjectMemberInfoRequest> membersInfo) {
        final Map<Long, User> users =
                userRepository
                        .findAllById(
                                membersInfo.stream()
                                        .map(ProjectMemberInfoRequest::userId)
                                        .distinct()
                                        .toList())
                        .stream()
                        .collect(Collectors.toMap(User::getId, user -> user));
        checkValidProjectMembers(membersInfo, users);
        return users;
    }

    private boolean checkRecruit(RecruitCounts recruitCounts, Boolean isRecruit) {
        final int total =
                recruitCounts.getBackendNum()
                        + recruitCounts.getFrontedNum()
                        + recruitCounts.getFullStackNum()
                        + recruitCounts.getDevOpsNum()
                        + recruitCounts.getDataEngineerNum();
        if (total < 0) {
            throw new IllegalArgumentException();
        }
        return !(total == 0 && isRecruit);
    }

    /**
     * 1. 전체 모집 인원 수 0명이면 IsRecruited: false 2. 요청자가 프로젝트 멤버인지 검증 - 아니면 예외 3. 리더 존재 여부 확인 - 아니면 예외
     * 4. TeamRole 검증 5. 업데이트 멤버 정렬 - toActive: 계속 활동하는 멤버, toInactive: 활동 중단하는 멤버, toIncoming: 신규
     * 멤버 6. 팀 스택 검증, 가공 7. mainImage 존재 여부 확인 7. 삭제하는 메인 이미지 확인 - 있으면 새로운 메인 이미지 있는지 확인 - 추가하는 메인
     * 이미지가 1개 초과면 에러 삭제하는 결과 이미지 확인 추가하는 결과 이미지 확인 팀 스택 검증 -> 스택 데이터 맵핑 active, inactive, incoming
     * 분류 업데이트 모집 안 하다 하는 경우 알람전송 인덱스 전송
     */
    @Transactional
    public ProjectTeamUpdateResponse update(
            Long projectTeamId,
            Long userId,
            List<String> mainImage,
            List<String> resultImages,
            ProjectTeamUpdateRequest request) {
        this.log.debug("UpdateProjectTeam: 시작");
        final TeamData teamData = request.getTeamData();
        final RecruitCounts recruitCounts = request.getRecruitCounts();
        final List<ProjectMemberInfoRequest> updateMembersInfo = request.getProjectMember();
        final List<TeamStackInfo.WithName> teamStacksInfo = request.getTeamStacks();
        final List<Long> deleteMembersId = request.getDeleteMembers();

        /* 1. 수정 권한이 있는 멤버인지 검증 */
        verifyUserIsActiveProjectMember(userId, projectTeamId);
        /* 2. TeamRole 검증 */
        validateLeaderExists(updateMembersInfo);
        /* 3. 프로젝트 이름 중복 검증 */
        checkUniqueProjectName(teamData.getName());
        /* 4. Recruit 준비 */
        final boolean wasRecruit = teamData.getIsRecruited();
        final boolean isRecruited = this.checkRecruit(recruitCounts, teamData.getIsRecruited());
        final ProjectTeam currentTeam =
                projectTeamRepository
                        .findById(projectTeamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        /* 4. TeamStack 검증 */
        if (!request.getTeamStacks().isEmpty()) {
            teamStackService.update(teamStacksInfo, currentTeam);
        }
        if (!mainImage.isEmpty()) {
            mainImgRepository.save(
                    ProjectMainImage.builder()
                            .imageUrl(mainImage.getFirst())
                            .projectTeam(currentTeam)
                            .build());
        }
        if (!resultImages.isEmpty()) {
            resultImgRepository.saveAll(
                    resultImages.stream()
                            .map(
                                    r ->
                                            ProjectResultImage.builder()
                                                    .imageUrl(r)
                                                    .projectTeam(currentTeam)
                                                    .build())
                            .toList());
        }
        final int resultImageCount = resultImgRepository.countByProjectTeamId(projectTeamId);
        if (resultImageCount - request.getDeleteResultImages().size() + resultImages.size() > 10) {
            throw new IllegalArgumentException();
        }
        final List<ProjectMember> existingMembers =
                projectMemberRepository.findAllByProjectTeamId(projectTeamId);

        /* 9. 업데이트 멤버 검증, 적용 / 신규 멤버 추출 */
        final List<ProjectMemberInfoRequest> incomingMembersInfo =
                updateExistMembersAndExtractIncomingMembers(
                        existingMembers, updateMembersInfo, deleteMembersId);

        /* 10. 프로젝트 팀 업데이트 */
        currentTeam.update(teamData, isRecruited);
        if (!incomingMembersInfo.isEmpty()) {
            final Map<Long, User> users = getLongUserMap(incomingMembersInfo);
            final List<ProjectMember> incomingMembers =
                    incomingMembersInfo.stream()
                            .map(
                                    m ->
                                            ProjectMember.builder()
                                                    .status(StatusCategory.APPROVED)
                                                    .summary("신규 회원 입니다.")
                                                    .teamRole(m.teamRole())
                                                    .isLeader(m.isLeader())
                                                    .projectTeam(currentTeam)
                                                    .user(users.get(m.userId()))
                                                    .build())
                            .toList();
            projectMemberRepository.saveAll(incomingMembers);
        }
        /* 12.isRecruited 값이 false → true 로 변경되었을 때 Slack 알림 전송 **/
        if (!wasRecruit && isRecruited) {
            // send Slack
            return ProjectTeamUpdateResponse.builder()
                    .id(projectTeamId)
                    .slackRequest(new SlackRequest())
                    .indexRequest(new IndexRequest())
                    .build();
        }

        /* 13. 인덱스 업데이트 */
        return ProjectTeamUpdateResponse.builder()
                .id(projectTeamId)
                .indexRequest(new IndexRequest())
                .build();
    }

    public List<ProjectMemberInfoRequest> updateExistMembersAndExtractIncomingMembers(
            List<ProjectMember> existingMembers,
            List<ProjectMemberInfoRequest> updateMember,
            List<Long> deleteMemberIds) {
        // 요청 중 수정하려는 유저 ID → 요청 정보 Map
        final Map<Long, ProjectMemberInfoRequest> updateMap =
                updateMember.stream()
                        .collect(
                                Collectors.toMap(
                                        ProjectMemberInfoRequest::userId, Function.identity()));
        final Set<Long> deleteIdSet = new HashSet<>(deleteMemberIds);
        applyMemberStateChanges(existingMembers, updateMap, deleteIdSet);

        return extractIncomingMembers(updateMap);
    }

    private void applyMemberStateChanges(
            List<ProjectMember> existingMembers,
            Map<Long, ProjectMemberInfoRequest> updateMap,
            Set<Long> deleteIdSet) {
        final Set<Integer> toActive = new HashSet<>();
        final Set<Integer> toInactive = new HashSet<>();
        for (ProjectMember member : existingMembers) {
            final Long userId = member.getUser().getId();
            final boolean markedForDelete = deleteIdSet.contains(member.getId());
            final ProjectMemberInfoRequest updateInfo = updateMap.get(userId);

            if (markedForDelete) {
                if (member.isDeleted()) {
                    throw new ProjectMemberNotFoundException();
                }
                if (updateInfo != null) {
                    throw new ProjectTeamDuplicateDeleteUpdateException();
                }
                member.toInactive();
                toInactive.add(member.getId().intValue());
                continue;
            }

            if (updateInfo != null) {
                member.toActive(updateInfo.teamRole(), updateInfo.isLeader());
                toActive.add(member.getId().intValue());
                updateMap.remove(userId);
            }
        }
        if (deleteIdSet.size() != toInactive.size()
                || toActive.size() + updateMap.size() != existingMembers.size()) {
            throw new ProjectTeamMissingUpdateMemberException();
        }
    }

    private List<ProjectMemberInfoRequest> extractIncomingMembers(
            Map<Long, ProjectMemberInfoRequest> remainingUpdateMap) {
        return List.copyOf(remainingUpdateMap.values());
    }

    private void checkUniqueProjectName(@Nullable String name) {
        if (name == null) {
            return;
        }
        if (projectTeamRepository.existsByName(name)) {
            throw new ProjectDuplicateTeamName();
        }
    }

    private void verifyUserIsActiveProjectMember(Long userId, Long projectTeamId) {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndDeletedFalseAndStatus(
                        userId, projectTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            throw new ProjectInvalidActiveRequester();
        }
    }

    @Transactional
    public void getDetailAndUpdateViewCount(Long projectTeamId) {
        final ProjectTeam pt = projectTeamRepository.findById(projectTeamId).orElseThrow();
        pt.increaseViewCount();
    }

    @Transactional(readOnly = true)
    public List<ProjectTeamGetAllResponse> getAllTeams(GetTeamQueryRequest request) {
        final List<TeamType> teamTypes = request.getTeamTypes();
        final List<PositionNumType> positionNumType = request.getPositionNumType();
        final Boolean isRecruited = request.getIsRecruited();
        final Boolean isFinished = request.getIsFinished();
        return getProjectResponses(teamTypes, positionNumType, isRecruited, isFinished);
    }

    private List<ProjectTeamGetAllResponse> getProjectResponses(
            List<TeamType> teamTypes,
            List<PositionNumType> positionNumType,
            Boolean isRecruited,
            Boolean isFinished) {
        if (teamTypes != null && !teamTypes.contains(TeamType.PROJECT)) {
            return Collections.emptyList();
        }
        return projectTeamDslRepository.fetchProjectTeams(positionNumType, isRecruited, isFinished);
    }

    public void close() {}

    public void delete() {}

    public void getUserProjects() {}

    public void getMembers() {}

    public void apply() {}

    public void cancelApplication() {}

    public void getApplicants() {}

    public void acceptApplicant() {}

    public void rejectApplicant() {}
}
