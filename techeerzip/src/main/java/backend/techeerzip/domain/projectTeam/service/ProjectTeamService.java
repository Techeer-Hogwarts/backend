package backend.techeerzip.domain.projectTeam.service;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.mapper.ProjectMemberMapper;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectMember.service.ProjectMemberService;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest;
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.projectTeam.exception.ProjectDuplicateTeamName;
import backend.techeerzip.domain.projectTeam.exception.ProjectExceededResultImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidProjectMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingLeaderException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamNotFoundException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamPositionClosedException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamRecruitmentClosedException;
import backend.techeerzip.domain.projectTeam.mapper.ProjectImageMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectIndexMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectSlackMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.mapper.TeamStackMapper;
import backend.techeerzip.domain.projectTeam.repository.ProjectMainImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectResultImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import backend.techeerzip.domain.projectTeam.repository.querydsl.ProjectTeamDslRepository;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectTeamService {

    private final ProjectMemberService projectMemberService;
    private final TeamStackService teamStackService;
    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectTeamDslRepository projectTeamDslRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectMainImageRepository mainImgRepository;
    private final ProjectResultImageRepository resultImgRepository;
    private final CustomLogger log;

    private static void ensureAllRequestedMembersExist(
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

    private static void validateLeaderExists(List<ProjectMemberInfoRequest> membersInfo) {
        if (membersInfo.stream().noneMatch(ProjectMemberInfoRequest::isLeader)) {
            throw new ProjectTeamMissingLeaderException();
        }
    }

    private static List<ProjectMember> mapToProjectMemberEntities(
            List<ProjectMemberInfoRequest> membersInfo,
            ProjectTeam teamEntity,
            Map<Long, User> users) {
        return membersInfo.stream()
                .map(
                        member ->
                                ProjectMemberMapper.toEntity(
                                        member,
                                        teamEntity,
                                        users.get(member.userId()),
                                        "초기 회원 입니다."))
                .toList();
    }

    private static Map<Long, ProjectMemberInfoRequest> toUserIdAndMemberInfoRequest(
            List<ProjectMemberInfoRequest> updateMember) {
        return updateMember.stream()
                .collect(
                        Collectors.toMap(
                                ProjectMemberInfoRequest::userId,
                                Function.identity(),
                                (oldVal, newVal) -> newVal));
    }

    /**
     * 프로젝트 팀을 생성합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>모집 조건과 이름 중복 유효성 검사
     *   <li>팀 스택, 프로젝트 멤버, 이미지 엔티티 변환
     *   <li>엔티티 저장 및 결과 응답 생성
     * </ol>
     *
     * @param mainImage 메인 이미지 URL 리스트 (필수, 1개만 허용됨)
     * @param resultImages 결과 이미지 URL 리스트 (선택, 최대 10개까지 허용)
     * @param request 팀 생성 요청 정보 (팀 정보, 모집 인원, 멤버, 스택 포함)
     * @return 프로젝트 팀 생성 결과 ID와 슬랙/인덱싱 요청 객체
     * @throws IllegalArgumentException 모집 인원이 음수이거나 isRecruited=true인데 인원수가 0인 경우
     * @throws ProjectDuplicateTeamName 팀 이름이 중복된 경우
     * @throws ProjectInvalidProjectMemberException 프로젝트 멤버가 존재하지 않거나 유효하지 않은 경우
     */
    @Transactional
    public ProjectTeamCreateResponse create(
            @NotEmpty List<String> mainImage, List<String> resultImages, ProjectTeamCreateRequest request) {
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

        final Map<Long, User> users = getIdAndUserMap(membersInfo);
        this.log.debug("CreateProjectTeam: 프로젝트 멤버 검증 완료");

        // 3. mapToEntities
        // ProjectTeam
        final ProjectTeam teamEntity =
                ProjectTeamMapper.toEntity(teamData, recruitCounts, isRecruited);
        final ProjectTeam team = projectTeamRepository.save(teamEntity);
        // ProjectMember
        final List<ProjectMember> memberEntities =
                mapToProjectMemberEntities(membersInfo, team, users);
        final ProjectMainImage mainImgEntity =
                ProjectImageMapper.toMainEntity(mainImage.getFirst(), team);
        final List<TeamStack> teamStackEntities =
                teamStacks.stream().map(s -> TeamStackMapper.toEntity(s, team)).toList();
        teamEntity.addProjectMembers(memberEntities);
        teamEntity.addProjectMainImages(List.of(mainImgEntity));

        if (!resultImages.isEmpty()) {
            final List<ProjectResultImage> resultImageEntities =
                    ProjectImageMapper.toResultEntities(resultImages, team);
            teamEntity.addProjectResultImages(resultImageEntities);
        }
        teamEntity.addTeamStacks(teamStackEntities);
        this.log.debug("CreateProjectTeam: 엔티티 맵핑 완료");

        final List<LeaderInfo> leaders = projectMemberService.getLeaders(team.getId());
        this.log.debug("CreateProjectTeam: 프로젝트 팀 생성 완료");

        // indexService, slackService
        // return 수정
        return new ProjectTeamCreateResponse(
                team.getId(),
                ProjectSlackMapper.toChannelRequest(team, leaders),
                ProjectIndexMapper.toIndexRequest(team));
    }

    /**
     * 요청된 프로젝트 멤버 ID 목록을 기반으로 User 엔티티를 조회하고, 존재하지 않는 유저가 있을 경우 예외를 던집니다.
     *
     * @param membersInfo 프로젝트 멤버 요청 정보 리스트
     * @return userId → User 엔티티 맵
     * @throws ProjectInvalidProjectMemberException 일부 유저가 존재하지 않을 경우
     */
    private Map<Long, User> getIdAndUserMap(List<ProjectMemberInfoRequest> membersInfo) {
        final Map<Long, User> users =
                getUsers(membersInfo).stream().collect(Collectors.toMap(User::getId, user -> user));
        ensureAllRequestedMembersExist(membersInfo, users);
        return users;
    }

    private List<User> getUsers(List<ProjectMemberInfoRequest> membersInfo) {
        return userRepository.findAllById(
                membersInfo.stream().map(ProjectMemberInfoRequest::userId).distinct().toList());
    }

    /**
     * 모집 인원 수를 기반으로 isRecruited 플래그의 유효성을 검사합니다.
     *
     * <p>모집 인원이 음수이거나, 총 모집 인원이 0인데도 모집 상태인 경우 예외를 던집니다.
     *
     * @param recruitCounts 포지션별 모집 인원 정보
     * @param isRecruit 모집 상태 플래그
     * @return 실제 모집 중인지 여부
     * @throws IllegalArgumentException 모집 인원이 음수이거나 불합리한 조합일 경우
     */
    private boolean checkRecruit(RecruitCounts recruitCounts, Boolean isRecruit) {
        final int total =
                recruitCounts.getBackendNum()
                        + recruitCounts.getFrontendNum()
                        + recruitCounts.getFullStackNum()
                        + recruitCounts.getDevOpsNum()
                        + recruitCounts.getDataEngineerNum();
        if (total < 0) {
            throw new IllegalArgumentException();
        }
        if (total == 0) {
            return false;
        }
        return isRecruit;
    }

    /**
     * 프로젝트 팀 정보를 수정합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>프로젝트 멤버 수정 권한 검증
     *   <li>리더 존재 여부 확인
     *   <li>팀 이름 중복 검증
     *   <li>팀 스택 및 이미지 검증 및 저장
     *   <li>기존 멤버 상태 업데이트 및 신규 멤버 등록
     *   <li>isRecruited가 변경되었을 경우 Slack 알림
     * </ol>
     *
     * @param projectTeamId 프로젝트 팀 ID
     * @param userId 요청한 사용자 ID
     * @param mainImage 메인 이미지 URL 리스트 (최대 1개)
     * @param resultImages 결과 이미지 URL 리스트 (최대 10개까지)
     * @param request 수정 요청 정보
     * @return 프로젝트 팀 수정 결과 응답 DTO
     * @throws ProjectInvalidActiveRequester 요청자가 팀의 활성 멤버가 아닌 경우
     * @throws ProjectTeamMissingLeaderException 리더가 존재하지 않는 경우
     * @throws ProjectDuplicateTeamName 팀 이름이 중복된 경우
     * @throws ProjectTeamNotFoundException 수정하려는 팀이 존재하지 않는 경우
     * @throws ProjectExceededResultImageException 결과 이미지 개수가 10개를 초과한 경우
     * @throws ProjectInvalidProjectMemberException 업데이트/삭제 멤버 정보가 잘못된 경우
     * @throws ProjectMemberNotFoundException 이미 삭제된 멤버를 다시 삭제하려는 경우
     * @throws ProjectTeamDuplicateDeleteUpdateException 동일한 멤버가 삭제와 업데이트 요청에 동시에 존재하는 경우
     * @throws ProjectTeamMissingUpdateMemberException 삭제/업데이트 대상 멤버가 실제와 불일치하는 경우
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
        final ProjectTeam team =
                projectTeamRepository
                        .findById(projectTeamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        /* 4. Recruit 준비 */
        final boolean wasRecruit = team.isRecruited();
        final boolean isRecruited = this.checkRecruit(recruitCounts, teamData.getIsRecruited());
        /* 3. 프로젝트 이름 중복 검증 */
        if (!teamData.getName().equals(team.getName())) {
            checkUniqueProjectName(teamData.getName());
        }
        /* 4. TeamStack 검증 */
        if (!request.getTeamStacks().isEmpty()) {
            team.clearTeamStacks();
            teamStackService.update(teamStacksInfo, team);
        }
        if (!mainImage.isEmpty()) {
            mainImgRepository.save(ProjectImageMapper.toMainEntity(mainImage.getFirst(), team));
        }
        if (!resultImages.isEmpty()) {
            resultImgRepository.saveAll(ProjectImageMapper.toResultEntities(resultImages, team));
        }
        validateResultImageCount(projectTeamId, resultImages, request);
        final List<ProjectMember> existingMembers =
                projectMemberRepository.findAllByProjectTeamId(projectTeamId);

        /* 9. 업데이트 멤버 검증, 적용 / 신규 멤버 추출 */
        final List<ProjectMemberInfoRequest> incomingMembersInfo =
                updateExistMembersAndExtractIncomingMembers(
                        existingMembers, updateMembersInfo, deleteMembersId);

        /* 10. 프로젝트 팀 업데이트 */
        team.update(teamData, isRecruited);
        if (!incomingMembersInfo.isEmpty()) {
            final Map<Long, User> users = getIdAndUserMap(incomingMembersInfo);
            final List<ProjectMember> incomingMembers =
                    ProjectMemberMapper.toEntities(incomingMembersInfo, team, users);
            projectMemberRepository.saveAll(incomingMembers);
        }
        /* 12.isRecruited 값이 false → true 로 변경되었을 때 Slack 알림 전송 **/
        if (!wasRecruit && isRecruited) {
            // send Slack
            final List<LeaderInfo> leaders = projectMemberService.getLeaders(team.getId());
            return ProjectTeamMapper.toUpdatedResponse(projectTeamId, team, leaders);
        }

        /* 13. 인덱스 업데이트 */
        return ProjectTeamMapper.toNoneSlackUpdateResponse(projectTeamId, team);
    }

    private void validateResultImageCount(
            Long projectTeamId, List<String> resultImages, ProjectTeamUpdateRequest request) {
        final int resultImageCount = resultImgRepository.countByProjectTeamId(projectTeamId);
        if (resultImageCount - request.getDeleteResultImages().size() + resultImages.size() > 10) {
            throw new ProjectExceededResultImageException();
        }
    }

    /**
     * 기존 프로젝트 멤버 리스트에서 변경 사항(업데이트/삭제)을 반영하고, 신규 멤버를 추출합니다.
     *
     * <p>업데이트 대상 멤버는 상태를 갱신하고, 삭제 대상 멤버는 비활성화 처리합니다. 이외에 요청 정보에 존재하지만 기존 멤버에 없는 신규 유저는 분리하여 반환됩니다.
     *
     * @param existingMembers 현재 저장된 프로젝트 멤버 리스트
     * @param updateMember 수정 요청에 포함된 프로젝트 멤버 정보
     * @param deleteMemberIds 삭제 요청된 멤버 ID 리스트
     * @return 신규로 추가될 프로젝트 멤버 요청 정보 리스트
     * @throws ProjectInvalidProjectMemberException 중복된 업데이트/삭제 ID가 존재하거나 일관되지 않은 경우
     * @throws ProjectMemberNotFoundException 이미 삭제된 멤버를 다시 삭제하려는 경우
     * @throws ProjectTeamDuplicateDeleteUpdateException 동일한 멤버가 삭제/업데이트 요청에 동시에 존재하는 경우
     * @throws ProjectTeamMissingUpdateMemberException 실제로 변경된 수와 요청 수가 불일치하는 경우
     */
    private List<ProjectMemberInfoRequest> updateExistMembersAndExtractIncomingMembers(
            List<ProjectMember> existingMembers,
            List<ProjectMemberInfoRequest> updateMember,
            List<Long> deleteMemberIds) {
        // 요청 중 수정하려는 유저 ID → 요청 정보 Map
        final Map<Long, ProjectMemberInfoRequest> updateMap =
                toUserIdAndMemberInfoRequest(updateMember);
        final Set<Long> deleteIdSet = new HashSet<>(deleteMemberIds);
        checkDuplicateUpdateMembers(updateMap, updateMember);
        checkDuplicateDeleteMembers(deleteIdSet, deleteMemberIds);
        applyMemberStateChanges(existingMembers, updateMap, deleteIdSet);

        return extractIncomingMembers(updateMap);
    }

    /**
     * 삭제 대상 멤버 ID 리스트에 중복이 존재하는지 검증합니다.
     *
     * @param deleteIdSet 중복 제거된 삭제 ID 집합
     * @param deleteMemberIds 원본 삭제 ID 리스트
     * @throws ProjectInvalidProjectMemberException 삭제 대상에 중복이 있을 경우
     */
    private void checkDuplicateDeleteMembers(Set<Long> deleteIdSet, List<Long> deleteMemberIds) {
        if (deleteIdSet.size() != deleteMemberIds.size()) {
            throw new ProjectInvalidProjectMemberException();
        }
    }

    /**
     * 업데이트 대상 멤버 정보 리스트에 중복된 userId가 있는지 검증합니다.
     *
     * @param updateMap userId → 요청 정보 맵
     * @param updateMember 원본 업데이트 요청 리스트
     * @throws ProjectInvalidProjectMemberException userId 기준 중복이 있는 경우
     */
    private void checkDuplicateUpdateMembers(
            Map<Long, ProjectMemberInfoRequest> updateMap,
            List<ProjectMemberInfoRequest> updateMember) {
        if (updateMap.size() != updateMember.size()) {
            throw new ProjectInvalidProjectMemberException();
        }
    }

    /**
     * 기존 멤버 목록을 기반으로 삭제 및 업데이트 요청을 처리합니다. 멤버의 상태를 active 또는 inactive로 변경합니다.
     *
     * @param existingMembers 기존 멤버 엔티티 리스트
     * @param updateMap 업데이트 대상 userId → 요청 정보
     * @param deleteIdSet 삭제할 멤버 ID 집합
     * @throws ProjectMemberNotFoundException 이미 삭제된 멤버를 다시 삭제하려는 경우
     * @throws ProjectTeamDuplicateDeleteUpdateException 동일한 멤버가 삭제와 업데이트에 동시에 포함된 경우
     * @throws ProjectTeamMissingUpdateMemberException 실제 처리 수와 요청 수가 불일치하는 경우
     */
    private void applyMemberStateChanges(
            List<ProjectMember> existingMembers,
            Map<Long, ProjectMemberInfoRequest> updateMap,
            Set<Long> deleteIdSet) {
        final Set<Integer> toActive = new HashSet<>();
        final Set<Integer> toInactive = new HashSet<>();
        final int updateCount = updateMap.size();
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
                member.softDelete();
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
                || toActive.size() + toInactive.size() != updateCount) {
            throw new ProjectTeamMissingUpdateMemberException();
        }
    }

    /**
     * updateMap에 남아 있는 멤버 요청 정보 중 신규 추가될 멤버만 반환합니다.
     *
     * @param remainingUpdateMap 기존 멤버에 없던 userId → 요청 정보
     * @return 신규 멤버 요청 리스트
     */
    private List<ProjectMemberInfoRequest> extractIncomingMembers(
            Map<Long, ProjectMemberInfoRequest> remainingUpdateMap) {
        return List.copyOf(remainingUpdateMap.values());
    }

    /**
     * 팀 이름이 중복되었는지 확인하고, 존재할 경우 예외를 던집니다.
     *
     * @param name 중복 확인할 팀 이름
     * @throws ProjectDuplicateTeamName 팀 이름이 이미 존재하는 경우
     */
    private void checkUniqueProjectName(String name) {
        final boolean exist = projectTeamRepository.existsByName(name);
        if (exist) {
            throw new ProjectDuplicateTeamName();
        }
    }

    /**
     * 주어진 유저가 해당 프로젝트 팀의 활성 멤버인지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param projectTeamId 프로젝트 팀 ID
     * @throws ProjectInvalidActiveRequester 활성 멤버가 아닌 경우
     */
    private void verifyUserIsActiveProjectMember(Long userId, Long projectTeamId) {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        userId, projectTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            throw new ProjectInvalidActiveRequester();
        }
    }

    /**
     * 프로젝트 팀의 조회 수를 1 증가시키고, 상세 정보를 반환합니다.
     *
     * @param projectTeamId 조회할 프로젝트 팀 ID
     * @return 프로젝트 팀 상세 정보
     * @throws java.util.NoSuchElementException 팀이 존재하지 않는 경우
     */
    @Transactional
    public ProjectTeamDetailResponse updateViewCountAndGetDetail(Long projectTeamId) {
        final ProjectTeam project = projectTeamRepository.findById(projectTeamId).orElseThrow();
        project.increaseViewCount();
        final List<ProjectMember> pm = project.getProjectMembers();
        return ProjectTeamMapper.toDetailResponse(project, pm);
    }

    /**
     * 주어진 ID 목록에 해당하는 팀들을 최신 순으로 조회합니다.
     *
     * @param keys 조회할 프로젝트 팀 ID 리스트
     * @param isRecruited 모집 여부 필터
     * @param isFinished 종료 여부 필터
     * @return 프로젝트 팀 목록
     */
    @Transactional(readOnly = true)
    public List<ProjectTeamGetAllResponse> getYoungTeamsById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        return projectTeamDslRepository.findManyYoungTeamById(keys, isRecruited, isFinished);
    }

    /**
     * 필터 조건에 맞는 영팀(Young Team) 목록을 페이징 형식으로 조회합니다.
     *
     * @param numTypes 포지션 필터 (백엔드, 프론트엔드 등)
     * @param isRecruited 모집 여부 필터
     * @param isFinished 종료 여부 필터
     * @param limit 최대 반환 개수
     * @return 조건에 맞는 프로젝트 팀 목록
     */
    @Transactional(readOnly = true)
    public List<ProjectTeamGetAllResponse> getYoungTeams(
            List<PositionNumType> numTypes, Boolean isRecruited, Boolean isFinished, Long limit) {
        return projectTeamDslRepository.sliceYoungTeam(numTypes, isRecruited, isFinished, limit);
    }

    /**
     * 프로젝트 팀의 모집을 종료합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자 ID
     * @throws ProjectInvalidActiveRequester 요청자가 활성 멤버가 아닌 경우
     * @throws ProjectTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public void close(Long teamId, Long userId) {
        projectMemberService.checkActive(teamId, userId);

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        pt.close();
    }

    /**
     * 프로젝트 팀을 소프트 삭제합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자 ID
     * @throws ProjectInvalidActiveRequester 요청자가 팀의 활성 멤버가 아닌 경우
     * @throws ProjectTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public void softDelete(Long teamId, Long userId) {
        projectMemberService.checkActive(teamId, userId);

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        pt.softDelete();
    }

    //    //삭제 대기
    //    public List<ProjectUserTeamsResponse> getUserTeams(Long userId) {
    //        return projectTeamDslRepository.findAllTeamsByUserId(userId);
    //    }

    //    //삭제 대기
    //    public void getMembers() {
    //    }

    /**
     * 프로젝트 팀에 새로운 멤버로 지원합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>팀 존재 여부 확인
     *   <li>팀의 모집 상태 및 포지션 유효성 확인
     *   <li>지원자 등록
     *   <li>Slack 알림 전송
     * </ol>
     *
     * @param request 지원 요청 정보 (팀 ID, 포지션, 자기소개 등)
     * @param applicantId 지원자 ID
     * @return Slack 알림 요청 DTO
     * @throws ProjectTeamNotFoundException 해당 팀이 존재하지 않는 경우
     * @throws ProjectTeamRecruitmentClosedException 팀이 모집 중이 아닌 경우
     * @throws ProjectTeamPositionClosedException 해당 포지션의 모집이 종료된 경우
     */
    @Transactional
    public List<SlackRequest.DM> apply(ProjectTeamApplyRequest request, Long applicantId) {
        final Long teamId = request.projectTeamId();
        final TeamRole teamRole = request.teamRole();
        final String summary = request.summary();

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        if (!pt.isRecruited()) {
            throw new ProjectTeamRecruitmentClosedException();
        }
        if (!pt.isRecruitPosition(teamRole)) {
            throw new ProjectTeamPositionClosedException();
        }
        final ProjectMember pm =
                projectMemberService.applyApplicant(pt, applicantId, teamRole, summary);
        final List<LeaderInfo> leaders = pt.getLeaders();
        final String applicantEmail = pm.getUser().getEmail();

        return ProjectSlackMapper.toDmRequest(pt, leaders, applicantEmail, StatusCategory.PENDING);
    }

    /**
     * 사용자의 프로젝트 팀 지원을 취소합니다.
     *
     * <p>지원 정보를 삭제하고, 팀 리더에게 Slack 알림을 전송합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param applicantId 지원자 ID
     * @return Slack 알림 요청 DTO
     * @throws ProjectMemberNotFoundException 지원 정보가 존재하지 않는 경우
     */
    @Transactional
    public List<SlackRequest.DM> cancelApplication(Long teamId, Long applicantId) {
        final ProjectMember pm =
                projectMemberRepository
                        .findByProjectTeamIdAndUserId(teamId, applicantId)
                        .orElseThrow(ProjectMemberNotFoundException::new);
        final String applicantEmail = pm.getUser().getEmail();
        projectMemberRepository.delete(pm);
        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        final List<LeaderInfo> leaders = pt.getLeaders();
        return ProjectSlackMapper.toDmRequest(
                pt, leaders, applicantEmail, StatusCategory.CANCELLED);
    }

    /**
     * 프로젝트 팀의 지원자 목록을 조회합니다.
     *
     * <p>요청자가 해당 팀의 활성 멤버가 아닐 경우 빈 리스트를 반환합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자(멤버) ID
     * @return 지원자 목록
     */
    public List<ProjectMemberApplicantResponse> getApplicants(Long teamId, Long userId) {
        if (!projectMemberService.isActive(teamId, userId)) {
            return List.of();
        }
        return projectMemberService.getApplicants(teamId);
    }

    /**
     * 프로젝트 팀의 지원자를 승인합니다.
     *
     * <p>지원자가 승인되면 상태가 PENDING → APPROVED로 변경되며, 관련 후속 처리를 수행합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자(팀 멤버) ID
     * @param applicantId 승인할 지원자 ID
     * @throws ProjectInvalidActiveRequester 요청자가 프로젝트 팀의 활성 멤버가 아닌 경우
     */
    public List<SlackRequest.DM> acceptApplicant(Long teamId, Long userId, Long applicantId) {
        verifyUserIsActiveProjectMember(teamId, userId);
        final String applicantEmail = projectMemberService.acceptApplicant(teamId, applicantId);
        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        final List<LeaderInfo> leaders = pt.getLeaders();

        return ProjectSlackMapper.toDmRequest(pt, leaders, applicantEmail, StatusCategory.APPROVED);
    }

    /**
     * 프로젝트 팀의 지원자를 거절합니다.
     *
     * <p>지원자의 상태를 제거하거나 거절 처리하며 후속 Slack 알림 등을 처리합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자(팀 멤버) ID
     * @param applicantId 거절할 지원자 ID
     * @throws ProjectInvalidActiveRequester 요청자가 프로젝트 팀의 활성 멤버가 아닌 경우
     */
    public List<SlackRequest.DM> rejectApplicant(Long teamId, Long userId, Long applicantId) {
        verifyUserIsActiveProjectMember(teamId, userId);
        final String applicantEmail = projectMemberService.rejectApplicant(teamId, applicantId);
        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        final List<LeaderInfo> leaders = pt.getLeaders();

        return ProjectSlackMapper.toDmRequest(pt, leaders, applicantEmail, StatusCategory.REJECT);
    }
}
