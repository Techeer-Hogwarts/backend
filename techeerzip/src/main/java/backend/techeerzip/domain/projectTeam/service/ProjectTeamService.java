package backend.techeerzip.domain.projectTeam.service;

import static backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepositoryImpl.ensureMaxSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.exception.TeamInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.mapper.ProjectMemberMapper;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectMember.service.ProjectMemberService;
import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.projectTeam.exception.ProjectDuplicateTeamName;
import backend.techeerzip.domain.projectTeam.exception.ProjectExceededResultImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidProjectMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMainImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingLeaderException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamNotFoundException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamPositionClosedException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamRecruitmentClosedException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamResultImageException;
import backend.techeerzip.domain.projectTeam.exception.TeamApplicantApplyException;
import backend.techeerzip.domain.projectTeam.exception.TeamApplicantCancelException;
import backend.techeerzip.domain.projectTeam.exception.TeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.TeamInvalidRecruitNumException;
import backend.techeerzip.domain.projectTeam.exception.TeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.mapper.ProjectImageMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectSlackMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamStackMapper;
import backend.techeerzip.domain.projectTeam.mapper.TeamIndexMapper;
import backend.techeerzip.domain.projectTeam.repository.ProjectMainImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectResultImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import backend.techeerzip.domain.projectTeam.repository.querydsl.ProjectTeamDslRepository;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectTeamService는 프로젝트 팀의 생성, 수정, 지원, 모집 종료 등의 핵심 비즈니스 로직을 처리합니다.
 *
 * <p>지원자 관리, 팀 이미지 처리, 팀 멤버 유효성 검사, 슬랙/인덱싱 연동까지 포함합니다.
 *
 * <p>ProjectTeamService 메서드는 다음과 같은 역할을 수행합니다:
 *
 * <ul>
 *   <li>create(...) - 프로젝트 팀 생성
 *   <li>update(...) - 프로젝트 팀 수정
 *   <li>close(...) - 모집 종료
 *   <li>softDelete(...) - 팀 소프트 삭제
 *   <li>apply(...) - 팀 지원
 *   <li>cancelApplication(...) - 팀 지원 취소
 *   <li>getApplicants(...) - 지원자 조회
 *   <li>acceptApplicant(...) - 지원자 승인
 *   <li>rejectApplicant(...) - 지원자 거절
 *   <li>getSliceTeams(...) - 팀 목록 페이징 조회
 *   <li>updateViewCountAndGetDetail(...) - 조회수 증가 및 상세 반환
 * </ul>
 *
 * @author Generated
 */
@Slf4j
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

    public static <T> void validateLeaderExists(
            List<T> membersInfo, Predicate<T> isLeaderPredicate) {
        boolean hasLeader = membersInfo.stream().anyMatch(isLeaderPredicate);
        if (!hasLeader) {
            log.error("ProjectTeamService: 프로젝트 팀에 리더가 존재하지 않습니다");
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

    public static <T> Map<Long, T> toUserIdAndMemberInfoRequest(
            List<T> updateMember, Function<T, Long> function) {
        return updateMember.stream()
                .collect(
                        Collectors.toMap(
                                function, Function.identity(), (oldVal, newVal) -> newVal));
    }

    /**
     * 프로젝트 팀을 생성합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>모집 인원 및 팀 이름 중복 여부 검증
     *   <li>팀 스택과 멤버, 이미지 정보를 엔티티로 변환
     *   <li>DB에 저장 후 슬랙/검색 인덱스용 객체로 응답
     * </ol>
     *
     * @param mainImage 메인 이미지 URL 리스트 (필수, 1개만 허용)
     * @param resultImages 결과 이미지 URL 리스트 (선택, 최대 10개까지 허용)
     * @param request 팀 생성 요청 정보 (팀 정보, 모집 인원, 멤버, 스택 포함)
     * @return 프로젝트 팀 생성 결과 ID와 슬랙/인덱싱 요청 객체
     * @throws IllegalArgumentException 모집 인원이 음수이거나 isRecruited==true인데 인원 수가 0인 경우
     * @throws ProjectDuplicateTeamName 팀 이름이 중복된 경우
     * @throws ProjectInvalidProjectMemberException 멤버로 요청된 유저가 존재하지 않는 경우
     */
    @Transactional
    public ProjectTeamCreateResponse create(
            @NotEmpty List<String> mainImage,
            List<String> resultImages,
            ProjectTeamCreateRequest request) {
        log.info("CreateProjectTeam: 시작");
        final TeamData teamData = request.getTeamData();
        final RecruitCounts recruitCounts = request.getRecruitCounts();
        final List<ProjectMemberInfoRequest> membersInfo = request.getProjectMember();
        final List<TeamStackInfo.WithName> teamStacksInfo =
                Optional.ofNullable(request.getTeamStacks()).orElse(List.of());

        final boolean isRecruited = checkRecruit(recruitCounts, teamData.getIsRecruited());
        log.info(
                "CreateProjectTeam: 모집 여부 확인 완료 - isRecruitedRequest={}, resolved={}",
                teamData.getIsRecruited(),
                isRecruited);

        checkUniqueProjectName(teamData.getName());
        log.info("CreateProjectTeam: 팀 이름 중복 검증 완료 - name={}", teamData.getName());

        final List<TeamStackInfo.WithStack> teamStacks = teamStackService.create(teamStacksInfo);
        log.info("CreateProjectTeam: 팀 스택 생성 완료 - count={}", teamStacks.size());

        final Map<Long, User> users = getIdAndUserMap(membersInfo);
        log.info("CreateProjectTeam: 프로젝트 멤버 유저 조회 및 검증 완료 - userCount={}", users.size());

        final ProjectTeam teamEntity =
                ProjectTeamMapper.toEntity(teamData, recruitCounts, isRecruited);
        final ProjectTeam team = projectTeamRepository.save(teamEntity);
        log.info("CreateProjectTeam: 프로젝트 팀 엔티티 저장 완료 - id={}", team.getId());

        final List<ProjectMember> memberEntities =
                mapToProjectMemberEntities(membersInfo, team, users);
        teamEntity.addProjectMembers(memberEntities);
        log.info("CreateProjectTeam: 프로젝트 멤버 저장 완료 - count={}", memberEntities.size());

        if (mainImage.size() != 1) {
            log.error("CreateProjectTeam: 메인 이미지 개수 비정상 - count={}", mainImage.size());
            throw new ProjectTeamMainImageException();
        }
        final ProjectMainImage mainImgEntity =
                ProjectImageMapper.toMainEntity(mainImage.getFirst(), team);
        teamEntity.addProjectMainImages(List.of(mainImgEntity));
        log.info("CreateProjectTeam: 메인 이미지 저장 완료");

        if (!resultImages.isEmpty()) {
            final List<ProjectResultImage> resultImageEntities =
                    ProjectImageMapper.toResultEntities(resultImages, team);
            teamEntity.addProjectResultImages(resultImageEntities);
            log.info("CreateProjectTeam: 결과 이미지 저장 완료 - count={}", resultImageEntities.size());
        }

        if (!teamStacks.isEmpty()) {
            final List<TeamStack> teamStackEntities =
                    teamStacks.stream().map(s -> ProjectTeamStackMapper.toEntity(s, team)).toList();
            teamEntity.addTeamStacks(teamStackEntities);
            log.info("CreateProjectTeam: 팀 스택 엔티티 저장 완료 - count={}", teamStackEntities.size());
        }

        final List<LeaderInfo> leaders = projectMemberService.getLeaders(team.getId());

        log.info("CreateProjectTeam: 프로젝트 팀 생성 완료 - teamId={}", team.getId());
        return new ProjectTeamCreateResponse(
                team.getId(),
                ProjectSlackMapper.toChannelRequest(team, leaders),
                TeamIndexMapper.toProjectRequest(team));
    }

    /**
     * 프로젝트 멤버 요청 정보로부터 userId를 기준으로 User 엔티티를 조회합니다.
     *
     * <p>존재하지 않는 유저가 포함된 경우 예외가 발생합니다.
     *
     * @param membersInfo 프로젝트 멤버 요청 정보 리스트
     * @return userId를 키로 하는 User 엔티티 맵
     * @throws ProjectInvalidProjectMemberException 일부 유저가 존재하지 않을 경우
     */
    private Map<Long, User> getIdAndUserMap(List<ProjectMemberInfoRequest> membersInfo) {
        log.info("ProjectTeam getIdAndUserMap: 유저 조회 시작, memberCount={}", membersInfo.size());
        final Map<Long, User> users =
                getUsers(membersInfo).stream().collect(Collectors.toMap(User::getId, user -> user));
        log.info("ProjectTeam getIdAndUserMap: 유저 조회 완료, userCount={}", users.size());
        ensureAllRequestedMembersExist(membersInfo, users);
        return users;
    }

    private List<User> getUsers(List<ProjectMemberInfoRequest> membersInfo) {
        log.info(
                "ProjectTeam getUsers: 유저 ID 추출 시작, userIds={}",
                membersInfo.stream().map(ProjectMemberInfoRequest::userId).toList());
        return userRepository.findAllById(
                membersInfo.stream().map(ProjectMemberInfoRequest::userId).distinct().toList());
    }

    /**
     * 프로젝트 팀의 모집 인원 수와 모집 상태의 유효성을 확인합니다.
     *
     * <p>모집 인원이 음수이거나, 총 모집 인원이 0인데도 모집 상태인 경우 예외를 던집니다.
     *
     * @param recruitCounts 포지션별 모집 인원 수 객체
     * @param isRecruit 모집 상태
     * @return 유효한 경우 true, 모집 인원이 0이면 false
     * @throws TeamInvalidRecruitNumException 총 모집 인원이 음수일 경우
     */
    private boolean checkRecruit(RecruitCounts recruitCounts, Boolean isRecruit) {
        final int total =
                recruitCounts.getBackendNum()
                        + recruitCounts.getFrontendNum()
                        + recruitCounts.getFullStackNum()
                        + recruitCounts.getDevOpsNum()
                        + recruitCounts.getDataEngineerNum();
        log.info("ProjectTeam checkRecruit: 모집 인원 합계 계산 완료, totalRecruitNum={}", total);
        if (total < 0) {
            log.error("ProjectTeam checkRecruit: 총 모집 인원이 음수, 예외 발생 - total={}", total);
            throw new TeamInvalidRecruitNumException();
        }
        if (total == 0) {
            log.info("ProjectTeam checkRecruit: 모집 인원이 0, 모집 false");
            return false;
        }
        log.info("ProjectTeam checkRecruit: 모집 가능 상태 유지, isRecruit={}", isRecruit);
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
     * @throws TeamInvalidActiveRequester 요청자가 팀의 활성 멤버가 아닌 경우
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
        log.info("ProjectTeam update: 시작, teamId={}, userId={}", projectTeamId, userId);
        final TeamData teamData = request.getTeamData();
        final RecruitCounts recruitCounts = request.getRecruitCounts();
        final List<ProjectMemberInfoRequest> updateMembersInfo = request.getProjectMember();
        final List<TeamStackInfo.WithName> teamStacksInfo = request.getTeamStacks();
        final List<Long> deleteMainImageId = request.getDeleteMainImages();
        final Set<Long> deleteResultImageIds = new HashSet<>(request.getDeleteResultImages());
        final List<Long> deleteMembersId = request.getDeleteMembers();

        verifyUserIsActiveProjectMember(userId, projectTeamId);
        log.info("ProjectTeam update: 유저 검증 완료, userId={}", userId);

        validateLeaderExists(updateMembersInfo, ProjectMemberInfoRequest::isLeader);
        log.info(
                "ProjectTeam update: 리더 존재 검증 완료, leaderCount={}",
                updateMembersInfo.stream().filter(ProjectMemberInfoRequest::isLeader).count());

        final ProjectTeam team =
                projectTeamRepository
                        .findById(projectTeamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        validateResultImageCount(projectTeamId, resultImages, request);
        log.info(
                "ProjectTeam update: 결과 이미지 개수 검증 완료, newCount={}, deleteCount={}",
                resultImages.size(),
                request.getDeleteResultImages().size());

        final boolean wasRecruit = team.isRecruited();
        final boolean isRecruited = this.checkRecruit(recruitCounts, teamData.getIsRecruited());
        log.info(
                "ProjectTeam update: 모집 여부 확인 완료, wasRecruit={}, isRecruitedRequest={}, resolved={}",
                wasRecruit,
                teamData.getIsRecruited(),
                isRecruited);

        if (!teamData.getName().equals(team.getName())) {
            checkUniqueProjectName(teamData.getName());
            log.info(
                    "ProjectTeam update: 팀 이름 변경 확인 완료, oldName={}, newName={}",
                    team.getName(),
                    teamData.getName());
        }

        if (!request.getTeamStacks().isEmpty()) {
            team.clearTeamStacks();
            teamStackService.update(teamStacksInfo, team);
            log.info("ProjectTeam update: 팀 스택 업데이트 완료, newStackCount={}", teamStacksInfo.size());
        }

        if (!mainImage.isEmpty()) {
            if (!team.isMainImageId(deleteMainImageId.getFirst())) {
                throw new ProjectTeamMainImageException();
            }
            final ProjectMainImage image =
                    ProjectImageMapper.toMainEntity(mainImage.getFirst(), team);
            team.updateMainImage(image);
            log.info("ProjectTeam update: 메인 이미지 저장 완료, imageId={}", image.getId());
        }

        if (!deleteResultImageIds.isEmpty()) {
            if (!team.checkResultImage(deleteMainImageId)) {
                throw new ProjectTeamResultImageException();
            }
            team.deleteResultImages(deleteResultImageIds);
            log.info("ProjectTeam update: 결과 이미지 삭제 완료, imageIds={}", deleteResultImageIds);
        }

        if (!resultImages.isEmpty()) {
            final List<ProjectResultImage> images =
                    ProjectImageMapper.toResultEntities(resultImages, team);
            team.updateResultImage(images);
            log.info("ProjectTeam update: 결과 이미지 저장 완료, count={}", images.size());
        }

        final List<ProjectMember> existingMembers =
                projectMemberRepository.findAllByProjectTeamId(projectTeamId);
        log.info("ProjectTeam update: 기존 프로젝트 멤버 조회 완료, existingCount={}", existingMembers.size());

        final List<ProjectMemberInfoRequest> incomingMembersInfo =
                updateExistMembersAndExtractIncomingMembers(
                        existingMembers, updateMembersInfo, deleteMembersId);
        log.info("ProjectTeam update: 신규 멤버 추출 완료, incomingCount={}", incomingMembersInfo.size());

        team.update(teamData, isRecruited);
        log.info("ProjectTeam update: 팀 엔티티 업데이트 완료");

        if (!incomingMembersInfo.isEmpty()) {
            final Map<Long, User> users = getIdAndUserMap(incomingMembersInfo);
            final List<ProjectMember> incomingMembers =
                    ProjectMemberMapper.toEntities(incomingMembersInfo, team, users);
            projectMemberRepository.saveAll(incomingMembers);
            log.info("ProjectTeam update: 신규 멤버 저장 완료, count={}", incomingMembers.size());
        }
        /* 12.isRecruited 값이 false → true 로 변경되었을 때 Slack 알림 전송 **/
        if (!wasRecruit && isRecruited) {
            log.info("ProjectTeam update: 모집 상태 변경됨, Slack 알림 대상");
            final List<LeaderInfo> leaders = projectMemberService.getLeaders(team.getId());
            return ProjectTeamMapper.toUpdatedResponse(projectTeamId, team, leaders);
        }

        log.info("ProjectTeam update: 인덱스 업데이트만 수행, teamId={}", projectTeamId);
        return ProjectTeamMapper.toNoneSlackUpdateResponse(projectTeamId, team);
    }

    /**
     * 수정 시 추가/삭제 요청된 결과 이미지 수의 유효성을 확인합니다.
     *
     * @param projectTeamId 팀 ID
     * @param resultImages 새로 추가할 이미지 리스트
     * @param request 삭제할 이미지 정보 포함 요청 DTO
     * @throws ProjectExceededResultImageException 최종 이미지 개수가 10개를 초과할 경우
     */
    private void validateResultImageCount(
            Long projectTeamId, List<String> resultImages, ProjectTeamUpdateRequest request) {
        final int resultImageCount = resultImgRepository.countByProjectTeamId(projectTeamId);
        int deleteCount = request.getDeleteResultImages().size();
        int newCount = resultImages.size();
        int finalCount = resultImageCount - deleteCount + newCount;

        log.info(
                "ProjectTeam validateResultImageCount: 현재={}, 삭제예정={}, 추가예정={}, 최종={}",
                resultImageCount,
                deleteCount,
                newCount,
                finalCount);
        if (resultImageCount - request.getDeleteResultImages().size() + resultImages.size() > 10) {
            log.info(
                    "ProjectTeam validateResultImageCount: 결과 이미지 개수 초과 예외 발생 - limit=10, actual={}",
                    finalCount);
            throw new ProjectExceededResultImageException();
        }
    }

    /**
     * 기존 멤버 목록을 기반으로 삭제 및 수정 요청을 반영하고, 기존에 존재하지 않았던 신규 멤버 요청 정보를 추출합니다.
     *
     * <p>처리 순서:
     *
     * <ol>
     *   <li>업데이트 요청에 중복된 userId가 있는지 검증
     *   <li>삭제 요청에 중복된 memberId가 있는지 검증
     *   <li>기존 멤버 리스트를 순회하며 삭제 또는 수정 반영
     *   <li>수정과 삭제로 처리되지 않은 요청 정보는 신규 멤버로 간주하여 반환
     * </ol>
     *
     * @param existingMembers 현재 저장된 프로젝트 멤버 목록
     * @param updateMember 수정 요청에 포함된 프로젝트 멤버 정보
     * @param deleteMemberIds 삭제 요청된 멤버 ID 리스트
     * @return 신규로 추가될 프로젝트 멤버 요청 정보 리스트
     * @throws ProjectInvalidProjectMemberException updateMap 또는 deleteIdSet에 중복 ID가 존재할 경우
     * @throws ProjectMemberNotFoundException 삭제 요청된 멤버가 이미 삭제되어 있을 경우
     * @throws ProjectTeamDuplicateDeleteUpdateException 동일한 멤버가 수정 및 삭제 요청에 모두 존재할 경우
     * @throws ProjectTeamMissingUpdateMemberException 삭제/업데이트 대상 멤버가 실제와 불일치하는 경우
     */
    private List<ProjectMemberInfoRequest> updateExistMembersAndExtractIncomingMembers(
            List<ProjectMember> existingMembers,
            List<ProjectMemberInfoRequest> updateMember,
            List<Long> deleteMemberIds) {
        // 요청 중 수정하려는 유저 ID → 요청 정보 Map
        final Map<Long, ProjectMemberInfoRequest> updateMap =
                toUserIdAndMemberInfoRequest(updateMember, ProjectMemberInfoRequest::userId);
        final Set<Long> deleteIdSet = new HashSet<>(deleteMemberIds);

        log.info(
                "ProjectTeam updateExistMembersAndExtractIncomingMembers: 수정 요청 수={}, 삭제 요청 수={}",
                updateMap.size(),
                deleteIdSet.size());
        checkDuplicateUpdateMembers(updateMap, updateMember);
        checkDuplicateDeleteMembers(deleteIdSet, deleteMemberIds);
        log.info("ProjectTeam updateExistMembersAndExtractIncomingMembers: 중복 검증 완료");

        applyMemberStateChanges(existingMembers, updateMap, deleteIdSet);

        final List<ProjectMemberInfoRequest> incoming = extractIncomingMembers(updateMap);
        log.info(
                "ProjectTeam updateExistMembersAndExtractIncomingMembers: 신규 멤버 추출 완료, count={}",
                incoming.size());

        return incoming;
    }

    /**
     * 삭제 대상 멤버 ID 리스트에 중복된 값이 존재하는지 검증합니다.
     *
     * @param deleteIdSet 중복 제거된 삭제 ID 집합
     * @param deleteMemberIds 원본 삭제 ID 리스트
     * @throws ProjectInvalidProjectMemberException 삭제 대상에 중복이 있을 경우
     */
    public static void checkDuplicateDeleteMembers(
            Set<Long> deleteIdSet, List<Long> deleteMemberIds) {
        if (deleteIdSet.size() != deleteMemberIds.size()) {
            log.error(
                    "ProjectTeam checkDuplicateDeleteMembers: 삭제 요청 중 중복 발생 - unique={}, total={}",
                    deleteIdSet.size(),
                    deleteMemberIds.size());
            throw new TeamDuplicateDeleteUpdateException();
        }
    }

    /**
     * 수정 요청 멤버 리스트에 중복된 userId가 존재하는지 검증합니다.
     *
     * @param updateMap userId → 요청 정보 맵
     * @param updateMember 원본 수정 요청 리스트
     * @throws TeamMissingUpdateMemberException userId 기준 중복이 있는 경우
     */
    public static <T> void checkDuplicateUpdateMembers(
            Map<Long, T> updateMap, List<T> updateMember) {
        if (updateMap.size() != updateMember.size()) {
            log.error(
                    "ProjectTeam checkDuplicateUpdateMembers: 중복 userId 감지됨 - unique={}, total={}",
                    updateMap.size(),
                    updateMember.size());
            throw new TeamMissingUpdateMemberException();
        }
    }

    /**
     * 기존 멤버 리스트를 순회하면서 삭제 및 수정 요청을 실제로 적용합니다. 삭제 요청은 soft delete 처리되고, 수정 요청은 역할 및 리더 상태가 반영됩니다.
     *
     * <p>요청한 삭제 및 수정 수와 실제 처리된 수가 불일치할 경우 예외를 발생시킵니다.
     *
     * @param existingMembers 기존 프로젝트 멤버 리스트
     * @param updateMap 수정 요청 userId → 요청 정보 맵
     * @param deleteIdSet 삭제할 프로젝트 멤버 ID Set
     * @throws ProjectMemberNotFoundException 삭제 요청된 멤버가 이미 삭제된 경우
     * @throws ProjectTeamDuplicateDeleteUpdateException 동일한 멤버가 수정 및 삭제 요청에 모두 존재하는 경우
     * @throws ProjectTeamMissingUpdateMemberException 처리된 삭제/수정 수가 요청 수와 다를 경우
     */
    private void applyMemberStateChanges(
            List<ProjectMember> existingMembers,
            Map<Long, ProjectMemberInfoRequest> updateMap,
            Set<Long> deleteIdSet) {
        final Set<Integer> toActive = new HashSet<>();
        final Set<Integer> toInactive = new HashSet<>();
        final int updateCount = updateMap.size();
        log.info(
                "ProjectTeam applyMemberStateChanges: 요청된 수정 수={}, 삭제 수={}",
                updateCount,
                deleteIdSet.size());

        for (ProjectMember member : existingMembers) {
            final Long userId = member.getUser().getId();
            final boolean markedForDelete = deleteIdSet.contains(member.getId());
            final ProjectMemberInfoRequest updateInfo = updateMap.get(userId);

            if (markedForDelete) {
                if (member.isDeleted()) {
                    log.error(
                            "ProjectTeam applyMemberStateChanges: 이미 삭제된 멤버 삭제 시도 - memberId={}",
                            member.getId());
                    throw new ProjectMemberNotFoundException();
                }
                if (updateInfo != null) {
                    log.error(
                            "ProjectTeam applyMemberStateChanges: 동일 멤버가 수정/삭제 요청 모두 포함 - memberId={}",
                            member.getId());
                    throw new ProjectTeamDuplicateDeleteUpdateException();
                }
                member.softDelete();
                toInactive.add(member.getId().intValue());
                log.info(
                        "ProjectTeam applyMemberStateChanges: 멤버 soft delete 처리 - memberId={}",
                        member.getId());
                continue;
            }

            if (updateInfo != null) {
                member.toActive(updateInfo.teamRole(), updateInfo.isLeader());
                toActive.add(member.getId().intValue());
                updateMap.remove(userId);
                log.info(
                        "ProjectTeam applyMemberStateChanges: 멤버 상태 업데이트 - memberId={}, role={}, isLeader={}",
                        member.getId(),
                        updateInfo.teamRole(),
                        updateInfo.isLeader());
            }
        }

        final boolean allDeletesProcessed = deleteIdSet.size() == toInactive.size();
        final boolean allUpdatesAccountedFor = toActive.size() + updateMap.size() == updateCount;

        if (!allDeletesProcessed || !allUpdatesAccountedFor) {
            log.error(
                    "ProjectTeam applyMemberStateChanges: 멤버 상태 변경 일치 실패 - updateMap 남은={}, 삭제된={}, 기존={}, toActive={}",
                    updateMap.size(),
                    toInactive.size(),
                    existingMembers.size(),
                    toActive);
            throw new TeamMissingUpdateMemberException();
        }
        log.info(
                "ProjectTeam applyMemberStateChanges: 멤버 상태 변경 완료 - 활성화={}, 비활성화={}",
                toActive,
                toInactive);
    }

    /**
     * 수정 요청에서 처리되지 않은 멤버 요청 정보 중 신규 멤버를 반환합니다.
     *
     * @param remainingUpdateMap 기존 멤버가 아닌 요청 정보
     * @return 신규 멤버 요청 리스트
     */
    public static <T> List<T> extractIncomingMembers(Map<Long, T> remainingUpdateMap) {
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
            log.info("ProjectTeam checkUniqueProjectName: 팀 이름 중복 발생 - name={}", name);
            throw new ProjectDuplicateTeamName();
        }
        log.info("ProjectTeam checkUniqueProjectName: 팀 이름 중복 없음 - name={}", name);
    }

    /**
     * 주어진 유저가 해당 프로젝트 팀의 활성 멤버인지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param projectTeamId 프로젝트 팀 ID
     * @throws TeamInvalidActiveRequester 활성 멤버가 아닌 경우
     */
    private void verifyUserIsActiveProjectMember(Long userId, Long projectTeamId) {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        userId, projectTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            log.info(
                    "ProjectTeam verifyUserIsActiveProjectMember: 활성 멤버 아님 - userId={}, teamId={}",
                    userId,
                    projectTeamId);
            throw new TeamInvalidActiveRequester();
        }
        log.info(
                "ProjectTeam verifyUserIsActiveProjectMember: 활성 멤버 확인 완료 - userId={}, teamId={}",
                userId,
                projectTeamId);
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
        log.info("ProjectTeam updateViewCountAndGetDetail: 조회 시작 - teamId={}", projectTeamId);
        final ProjectTeam project = projectTeamRepository.findById(projectTeamId).orElseThrow();
        project.increaseViewCount();
        log.info(
                "ProjectTeam updateViewCountAndGetDetail: 조회 수 증가 완료 - teamId={}, newViewCount={}",
                projectTeamId,
                project.getViewCount());
        final List<ProjectMember> pm = project.getActiveMember();
        return ProjectTeamMapper.toDetailResponse(project, pm);
    }

    /**
     * 주어진 ID 목록에 해당하는 팀들을 최신 순으로 조회합니다.
     *
     * @param keys 조회할 프로젝트 팀 ID 리스트
     * @return 프로젝트 팀 목록
     */
    @Transactional(readOnly = true)
    public List<ProjectSliceTeamsResponse> getProjectTeamsById(List<Long> keys) {
        log.info("ProjectTeam getProjectTeamsById: 팀 목록 조회 시작, count={}", keys.size());
        final List<ProjectTeam> teams = projectTeamRepository.findAllById(keys);
        log.info("ProjectTeam getProjectTeamsById: 팀 목록 조회 완료, found={}", teams.size());
        return teams.stream().map(ProjectTeamMapper::toGetAllResponse).toList();
    }

    /**
     * 커서 기반으로 프로젝트 팀 목록을 조회하고 다음 커서 정보를 포함한 응답을 반환합니다.
     *
     * @param query 조회 조건을 담은 DTO
     * @return 팀 목록과 다음 커서 정보
     */
    @Transactional(readOnly = true)
    public GetAllTeamsResponse getSliceTeams(GetProjectTeamsQuery query) {
        log.info(
                "ProjectTeam getSliceTeams: 팀 목록 커서 기반 조회 시작 - limit={}, sortType={}",
                query.getLimit(),
                query.getSortType());

        final int limit = query.getLimit();
        final SortType sortType = query.getSortType();
        final List<ProjectTeam> teams = projectTeamDslRepository.sliceTeams(query);

        log.info("ProjectTeam getSliceTeams: 조회된 전체 팀 수={}", teams.size());

        final SliceNextCursor next = setNextInfo(teams, limit, sortType);
        final List<ProjectTeam> fitTeams = ensureMaxSize(teams, limit);

        log.info("ProjectTeam getSliceTeams: 페이징 처리된 팀 수={}", fitTeams.size());

        final List<SliceTeamsResponse> responses =
                new ArrayList<>(
                        fitTeams.stream().map(ProjectTeamMapper::toGetAllResponse).toList());

        return new GetAllTeamsResponse(responses, next);
    }

    /**
     * 조회된 팀 목록에서 다음 커서 정보를 생성합니다.
     *
     * @param sortedTeams 정렬된 팀 리스트
     * @param limit 요청한 페이징 제한 개수
     * @param sortType 정렬 기준
     * @return 다음 페이지 커서 정보 DTO
     */
    private static SliceNextCursor setNextInfo(
            List<ProjectTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            log.info(
                    "ProjectTeam setNextInfo: 다음 페이지 없음 - size={}, limit={}",
                    sortedTeams.size(),
                    limit);
            return SliceNextCursor.builder().hasNext(false).build();
        }
        final ProjectTeam last = sortedTeams.getLast();
        log.info(
                "ProjectTeam setNextInfo: 다음 커서 생성 - sortType={}, lastTeamId={}",
                sortType,
                last.getId());

        return getNextInfo(
                sortType,
                last.getId(),
                last.getUpdatedAt(),
                last.getViewCount(),
                last.getLikeCount());
    }

    /**
     * 정렬 기준에 따라 다음 커서 정보를 생성합니다.
     *
     * @param sortType 정렬 기준
     * @param id 마지막 요소의 ID
     * @param updatedAt 마지막 요소의 수정 시간
     * @param viewCount 마지막 요소의 조회 수
     * @param likeCount 마지막 요소의 좋아요 수
     * @return 커서 정보 DTO
     */
    public static SliceNextCursor getNextInfo(
            SortType sortType,
            Long id,
            LocalDateTime updatedAt,
            Integer viewCount,
            Integer likeCount) {
        log.info(
                "ProjectTeam getNextInfo: 커서 정보 생성 - sortType={}, id={}, updatedAt={}, viewCount={}, likeCount={}",
                sortType,
                id,
                updatedAt,
                viewCount,
                likeCount);

        return switch (sortType) {
            case SortType.UPDATE_AT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(id)
                            .dateCursor(updatedAt)
                            .sortType(sortType)
                            .build();
            case SortType.VIEW_COUNT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(id)
                            .countCursor(viewCount)
                            .sortType(sortType)
                            .build();
            case SortType.LIKE_COUNT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(id)
                            .countCursor(likeCount)
                            .sortType(sortType)
                            .build();
        };
    }

    /**
     * 프로젝트 팀의 모집을 종료합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자 ID
     * @throws TeamInvalidActiveRequester 요청자가 활성 멤버가 아닌 경우
     * @throws ProjectTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public void close(Long teamId, Long userId) {
        log.info("ProjectTeam close: 모집 종료 요청 - teamId={}, userId={}", teamId, userId);

        projectMemberService.checkActive(teamId, userId);

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        pt.close();

        log.info("ProjectTeam close: 모집 상태 종료 완료 - teamId={}", teamId);
    }

    /**
     * 프로젝트 팀을 소프트 삭제합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자 ID
     * @throws TeamInvalidActiveRequester 요청자가 팀의 활성 멤버가 아닌 경우
     * @throws ProjectTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public void softDelete(Long teamId, Long userId) {
        log.info("ProjectTeam softDelete: 소프트 삭제 요청 - teamId={}, userId={}", teamId, userId);

        projectMemberService.checkActive(teamId, userId);

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        pt.softDelete();
        log.info("ProjectTeam softDelete: 소프트 삭제 완료 - teamId={}", teamId);
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
    public List<ProjectSlackRequest.DM> apply(ProjectTeamApplyRequest request, Long applicantId) {
        final Long teamId = request.projectTeamId();
        final TeamRole teamRole = request.teamRole();
        final String summary = request.summary();

        log.info(
                "ProjectTeam apply: 지원 요청 시작 - teamId={}, applicantId={}, role={}",
                teamId,
                applicantId,
                teamRole);

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> {
                                    log.error("ProjectTeam apply: 존재하지 않는 팀 - teamId={}", teamId);
                                    return new ProjectTeamNotFoundException();
                                });

        if (!pt.isRecruited()) {
            log.error("ProjectTeam apply: 팀 모집 종료됨 - teamId={}", teamId);
            throw new ProjectTeamRecruitmentClosedException();
        }

        if (!pt.isRecruitPosition(teamRole)) {
            log.info("ProjectTeam apply: 포지션 모집 종료됨 - teamId={}, role={}", teamId, teamRole);
            throw new ProjectTeamPositionClosedException();
        }

        final ProjectMember pm =
                projectMemberService.applyApplicant(pt, applicantId, teamRole, summary);
        final String applicantEmail = pm.getUser().getEmail();
        final List<LeaderInfo> leaders = pt.getLeaders();

        log.info(
                "ProjectTeam apply: 지원 완료 - applicantId={}, email={}", applicantId, applicantEmail);

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
    public List<ProjectSlackRequest.DM> cancelApplication(Long teamId, Long applicantId) {
        log.info(
                "ProjectTeam cancelApplication: 지원 취소 요청 - teamId={}, applicantId={}",
                teamId,
                applicantId);

        final ProjectMember pm =
                projectMemberRepository
                        .findByProjectTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(
                                () -> {
                                    log.error(
                                            "ProjectTeam cancelApplication: 지원 정보 없음 - teamId={}, applicantId={}",
                                            teamId,
                                            applicantId);
                                    return new ProjectMemberNotFoundException();
                                });

        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(
                                () -> {
                                    log.error(
                                            "ProjectTeam cancelApplication: 팀 없음 - teamId={}",
                                            teamId);
                                    return new ProjectTeamNotFoundException();
                                });

        pt.remove(pm);
        final String applicantEmail = pm.getUser().getEmail();
        final List<LeaderInfo> leaders = pt.getLeaders();

        log.info("ProjectTeam cancelApplication: 지원 취소 완료 - email={}", applicantEmail);

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
        log.info("ProjectTeam getApplicants: 지원자 조회 요청 - teamId={}, userId={}", teamId, userId);

        // not active and not pending => 404
        final ProjectMember member =
                projectMemberService
                        .getMember(teamId, userId)
                        .orElseThrow(TeamApplicantApplyException::new);
        if (!member.isActive()) {
            if (!member.isPending()) {
                throw new TeamApplicantApplyException();
            }
            // not active and pending => 409
            throw new TeamApplicantCancelException();
        }

        // active and have no applicants => []
        // active and have applicants => [...]
        List<ProjectMemberApplicantResponse> applicants =
                projectMemberService.getApplicants(teamId);
        log.info("ProjectTeam getApplicants: 지원자 수 조회 완료 - count={}", applicants.size());

        return applicants;
    }

    /**
     * 프로젝트 팀의 지원자를 승인합니다.
     *
     * <p>지원자가 승인되면 상태가 PENDING → APPROVED로 변경되며, 관련 후속 처리를 수행합니다.
     *
     * @param teamId 프로젝트 팀 ID
     * @param userId 요청자(팀 멤버) ID
     * @param applicantId 승인할 지원자 ID
     * @throws TeamInvalidActiveRequester 요청자가 프로젝트 팀의 활성 멤버가 아닌 경우
     */
    @Transactional
    public List<ProjectSlackRequest.DM> acceptApplicant(
            Long teamId, Long userId, Long applicantId) {
        log.info(
                "ProjectTeam acceptApplicant: 지원자 승인 요청 - teamId={}, userId={}, applicantId={}",
                teamId,
                userId,
                applicantId);

        verifyUserIsActiveProjectMember(userId, teamId);
        final String applicantEmail = projectMemberService.acceptApplicant(teamId, applicantId);
        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        final List<LeaderInfo> leaders = pt.getLeaders();

        log.info("ProjectTeam acceptApplicant: 승인 완료 - applicantEmail={}", applicantEmail);

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
     * @throws TeamInvalidActiveRequester 요청자가 프로젝트 팀의 활성 멤버가 아닌 경우
     */
    @Transactional
    public List<ProjectSlackRequest.DM> rejectApplicant(
            Long teamId, Long userId, Long applicantId) {
        log.info(
                "ProjectTeam rejectApplicant: 지원자 거절 요청 - teamId={}, userId={}, applicantId={}",
                teamId,
                userId,
                applicantId);

        verifyUserIsActiveProjectMember(userId, teamId);
        final String applicantEmail = projectMemberService.rejectApplicant(teamId, applicantId);
        final ProjectTeam pt =
                projectTeamRepository
                        .findById(teamId)
                        .orElseThrow(ProjectTeamNotFoundException::new);
        final List<LeaderInfo> leaders = pt.getLeaders();

        log.info("ProjectTeam rejectApplicant: 거절 완료 - applicantEmail={}", applicantEmail);

        return ProjectSlackMapper.toDmRequest(pt, leaders, applicantEmail, StatusCategory.REJECT);
    }
}
