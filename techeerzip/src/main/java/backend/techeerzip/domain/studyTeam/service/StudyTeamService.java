package backend.techeerzip.domain.studyTeam.service;

import static backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepositoryImpl.ensureMaxSize;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.*;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.getNextInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.exception.TeamInvalidActiveRequester;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.exception.TeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.TeamInvalidRecruitNumException;
import backend.techeerzip.domain.projectTeam.exception.TeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.mapper.TeamIndexMapper;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyMember.exception.StudyMemberNotFoundException;
import backend.techeerzip.domain.studyMember.mapper.StudyMemberMapper;
import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import backend.techeerzip.domain.studyMember.service.StudyMemberService;
import backend.techeerzip.domain.studyTeam.dto.request.StudyData;
import backend.techeerzip.domain.studyTeam.dto.request.StudyMemberInfoRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamCreateResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamUpdateResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.exception.StudyExceededResultImageException;
import backend.techeerzip.domain.studyTeam.exception.StudyTeamDuplicateException;
import backend.techeerzip.domain.studyTeam.exception.StudyTeamNotFoundException;
import backend.techeerzip.domain.studyTeam.exception.StudyTeamRecruitmentClosedException;
import backend.techeerzip.domain.studyTeam.mapper.StudyImageMapper;
import backend.techeerzip.domain.studyTeam.mapper.StudySlackMapper;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;
import backend.techeerzip.domain.studyTeam.repository.StudyImageRepository;
import backend.techeerzip.domain.studyTeam.repository.StudyTeamRepository;
import backend.techeerzip.domain.studyTeam.repository.querydsl.StudyTeamDslRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.service.UserService;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {

    private final StudyTeamRepository studyTeamRepository;
    private final StudyTeamDslRepository studyTeamDslRepository;
    private final StudyImageRepository studyImageRepository;
    private final StudyMemberService studyMemberService;
    private final StudyMemberRepository studyMemberRepository;
    private final UserService userService;

    /**
     * 스터디 팀을 생성하고, 이미지 및 멤버 정보를 저장한 후 생성 결과를 반환합니다.
     *
     * <p>처리 흐름:
     *
     * <ol>
     *   <li>중복된 스터디 이름이 있는지 검증합니다.
     *   <li>모집 여부 및 인원 수를 확인합니다.
     *   <li>리더 존재 여부를 확인합니다.
     *   <li>StudyTeam 엔티티를 저장합니다.
     *   <li>결과 이미지가 있다면 저장합니다.
     *   <li>StudyMember를 생성하고 저장합니다.
     * </ol>
     *
     * @param resultUrls 결과 이미지 URL 리스트
     * @param request 스터디 생성 요청 DTO
     * @return 생성된 스터디 정보와 슬랙 요청 정보
     * @throws StudyTeamDuplicateException 스터디 이름이 중복된 경우
     * @throws TeamInvalidRecruitNumException 모집 인원 수가 유효하지 않은 경우
     */
    @Transactional
    public StudyTeamCreateResponse create(List<String> resultUrls, StudyTeamCreateRequest request) {
        log.info("스터디 생성 요청 시작 - 이름: {}", request.getStudyData().getName());

        final StudyData studyData = request.getStudyData();
        final List<StudyMemberInfoRequest> membersInfo = request.getStudyMember();

        checkUniqueStudyName(studyData.getName());
        log.info("중복 이름 검증 완료 - {}", studyData.getName());

        boolean isRecruited = checkRecruit(studyData.getIsRecruited(), studyData.getRecruitNum());
        log.info("모집 여부 확인 완료 - isRecruited: {}, recruitNum: {}", studyData.getIsRecruited(), studyData.getRecruitNum());

        validateLeaderExists(membersInfo, StudyMemberInfoRequest::getIsLeader);
        log.info("리더 존재 여부 확인 완료");

        final Map<Long, User> users =
                userService.getIdAndUserMap(membersInfo, StudyMemberInfoRequest::getUserId);
        log.info("사용자 정보 조회 완료 - 사용자 수: {}", users.size());

        final StudyTeam team =
                studyTeamRepository.save(StudyTeamMapper.toEntity(studyData, isRecruited));
        log.info("스터디 팀 저장 완료 - teamId: {}", team.getId());

        if (!resultUrls.isEmpty()) {
            studyImageRepository.saveAll(StudyImageMapper.toManyResultEntity(resultUrls, team));
            log.info("결과 이미지 저장 완료 - 이미지 수: {}", resultUrls.size());
        }

        final List<StudyMember> memberEntities =
                membersInfo.stream()
                        .map(
                                info ->
                                        StudyMemberMapper.toEntity(
                                                info, team, users.get(info.getUserId())))
                        .toList();
        final List<StudyMember> members = studyMemberRepository.saveAll(memberEntities);
        log.info("스터디 멤버 저장 완료 - 멤버 수: {}", members.size());

        final List<LeaderInfo> leaders = extractLeaders(members);
        log.info("스터디 생성 완료 - teamId: {}", team.getId());

        return new StudyTeamCreateResponse(
                team.getId(),
                StudySlackMapper.toChannelRequest(team, leaders),
                TeamIndexMapper.toStudyRequest(team));
    }

    private static List<LeaderInfo> extractLeaders(List<StudyMember> members) {
        final List<LeaderInfo> leaders = members.stream()
                .filter(StudyMember::isLeader)
                .map(m -> new LeaderInfo(m.getUser().getName(), m.getUser().getEmail()))
                .toList();

        log.info("extractLeaders: 전체 멤버 수 = {}, 리더 수 = {}", members.size(), leaders.size());
        return leaders;
    }

    private static boolean checkRecruit(Boolean isRecruited, Integer recruitNum) {
        log.info("checkRecruit: isRecruited = {}, recruitNum = {}", isRecruited, recruitNum);
        if (recruitNum < 0) {
            log.error("checkRecruit: recruitNum이 0보다 작음 → 예외 발생");
            throw new TeamInvalidRecruitNumException();
        }
        if (recruitNum == 0) {
            log.info("checkRecruit: recruitNum == 0 → 모집 비활성 상태로 간주");
            return false;
        }
        return isRecruited;
    }

    private void checkUniqueStudyName(String name) {
        log.info("checkUniqueStudyName: 팀 이름 중복 여부 확인 - {}", name);
        final boolean exist = studyTeamRepository.existsByName(name);
        if (exist) {
            log.error("checkUniqueStudyName: '{}' 이름이 이미 존재함 → 예외 발생", name);
            throw new StudyTeamDuplicateException();
        }
    }

    /**
     * 주어진 조회 조건에 따라 스터디 팀 목록을 슬라이스 방식으로 조회합니다.
     *
     * <p>처리 흐름: 1. 조건에 맞는 스터디 팀을 조회합니다. 2. next 커서를 계산합니다. 3. 지정된 limit 수만큼 잘라 응답합니다.
     *
     * @param query 조회 조건 DTO
     * @return 스터디 팀 목록 및 next 커서
     */
    public GetAllTeamsResponse getSliceTeams(GetStudyTeamsQuery query) {
        final int limit = query.getLimit();
        final SortType sortType = query.getSortType();
        log.info("getSliceTeams: 요청 - sortType = {}, limit = {}", sortType, limit);

        final List<StudyTeam> teams = studyTeamDslRepository.sliceTeams(query);
        log.info("getSliceTeams: 조회된 팀 수 = {}", teams.size());

        final SliceNextCursor next = setNextInfo(teams, limit, sortType);
        final List<StudyTeam> fitTeams = ensureMaxSize(teams, limit);

        final List<SliceTeamsResponse> responses =
                new ArrayList<>(fitTeams.stream().map(StudyTeamMapper::toGetAllResponse).toList());

        log.info("getSliceTeams: 응답 팀 수 = {}, nextCursor.hasNext = {}", responses.size(), next.getHasNext());
        return new GetAllTeamsResponse(responses, next);
    }

    /**
     * ID 리스트에 해당하는 스터디 팀들을 조회하고 간략한 정보를 반환합니다.
     *
     * @param keys 스터디 팀 ID 리스트
     * @return 각 팀의 간략한 응답 DTO 리스트
     */
    public List<StudySliceTeamsResponse> getStudyTeamsById(List<Long> keys) {
        log.info("getStudyTeamsById: 요청된 ID 수 = {}", keys.size());
        final List<StudyTeam> teams = studyTeamRepository.findAllById(keys);
        final List<StudySliceTeamsResponse> result = teams.stream()
                .map(StudyTeamMapper::toGetAllResponse)
                .toList();
        log.info("getStudyTeamsById: 응답 수 = {}", result.size());
        return result;
    }

    private static SliceNextCursor setNextInfo(
            List<StudyTeam> sortedTeams, Integer limit, SortType sortType) {
        log.info("setNextInfo: 팀 수 = {}, limit = {}, sortType = {}", sortedTeams.size(), limit, sortType);
        if (sortedTeams.size() <= limit) {
            log.info("setNextInfo: hasNext = false (limit 이하)");
            return SliceNextCursor.builder().hasNext(false).build();
        }

        final StudyTeam last = sortedTeams.getLast();
        log.info("setNextInfo: 다음 커서 생성 - lastId = {}, updatedAt = {}, viewCount = {}, likeCount = {}",
                last.getId(), last.getUpdatedAt(), last.getViewCount(), last.getLikeCount());

        return getNextInfo(
                sortType,
                last.getId(),
                last.getUpdatedAt(),
                last.getViewCount(),
                last.getLikeCount());
    }

    /**
     * 스터디 팀의 정보를 수정하고, 이미지/멤버 변경 사항을 반영합니다.
     *
     * <p>처리 흐름:
     *
     * <ol>
     *   <li>사용자의 멤버 자격 확인
     *   <li>리더 존재 여부 확인
     *   <li>결과 이미지 개수 유효성 검사
     *   <li>팀명 변경 시 중복 여부 확인
     *   <li>삭제 요청된 이미지 삭제
     *   <li>새 이미지 추가
     *   <li>기존 멤버 상태 변경 및 신규 멤버 분리
     *   <li>팀 정보 업데이트
     *   <li>리크루트 변경 시 슬랙 요청 포함 응답
     * </ol>
     *
     * @param studyTeamId 수정할 팀 ID
     * @param userId 요청자 ID
     * @param resultImageUrls 추가할 결과 이미지 URL
     * @param request 수정 요청 DTO
     * @return 변경된 스터디 팀 정보
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws StudyTeamDuplicateException 변경할 팀명이 중복된 경우
     * @throws TeamInvalidActiveRequester 요청자가 승인된 멤버가 아닌 경우
     * @throws TeamInvalidRecruitNumException 모집 인원 수가 유효하지 않은 경우
     * @throws StudyExceededResultImageException 최대 이미지 개수를 초과한 경우
     * @throws StudyMemberNotFoundException 삭제 요청된 멤버가 이미 삭제된 경우
     * @throws TeamDuplicateDeleteUpdateException 삭제와 수정에 동시에 포함된 멤버가 존재하는 경우
     * @throws TeamMissingUpdateMemberException 요청 수와 처리 수가 일치하지 않는 경우
     */
    @Transactional
    public StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<String> resultImageUrls,
            StudyTeamUpdateRequest request) {
        log.info("StudyTeam update 시작 - studyTeamId={}, userId={}", studyTeamId, userId);

        final List<StudyMemberInfoRequest> updateMembersInfo = request.getStudyMember();
        final StudyData studyData = request.getStudyData();
        final List<Long> deleteResultImages = request.getDeleteImages();
        final List<Long> deleteMembersId = request.getDeleteMembers();

        log.info("StudyTeam update: 요청 멤버 수={}, 삭제 멤버 수={}, 추가 이미지 수={}, 삭제 이미지 수={}",
                updateMembersInfo.size(), deleteMembersId.size(), resultImageUrls.size(), deleteResultImages.size());
        verifyUserIsActiveStudyMember(studyTeamId, userId);
        log.info("StudyTeam update: 요청자 승인 멤버 검증 완료");

        validateLeaderExists(request.getStudyMember(), StudyMemberInfoRequest::getIsLeader);
        log.info("StudyTeam update: 리더 존재 검증 완료");

        validateResultImageCount(studyTeamId, resultImageUrls, deleteMembersId);
        log.info("StudyTeam update: 결과 이미지 수 검증 완료");

        final StudyTeam team =
                studyTeamRepository
                        .findById(studyTeamId)
                        .orElseThrow(StudyTeamNotFoundException::new);

        final boolean wasRecruit = team.getIsRecruited();
        final boolean isRecruited = checkRecruit(team.getIsRecruited(), studyData.getRecruitNum());
        log.info("StudyTeam update: 모집 상태 변경 여부 - wasRecruit={}, isRecruited={}", wasRecruit, isRecruited);

        if (!team.isSameName(studyData.getName())) {
            checkUniqueStudyName(studyData.getName());
            log.info("StudyTeam update: 팀명 변경 - 기존={}, 요청={}", team.getName(), studyData.getName());
        }

        if (!deleteResultImages.isEmpty()) {
            studyImageRepository.deleteAllById(deleteResultImages);
            log.info("StudyTeam update: 결과 이미지 삭제 완료 - 삭제 수={}", deleteResultImages.size());
        }

        if (!resultImageUrls.isEmpty()) {
            final List<StudyResultImage> images =
                    StudyImageMapper.toManyResultEntity(resultImageUrls, team);
            team.addResultImage(images);
            log.info("StudyTeam update: 결과 이미지 추가 완료 - 추가 수={}", resultImageUrls.size());
        }

        final List<StudyMember> existingMembers =
                studyMemberRepository.findAllByStudyTeamId(studyTeamId);
        log.info("StudyTeam update: 기존 멤버 조회 완료 - count={}", existingMembers.size());

        final List<StudyMemberInfoRequest> incomingMembersInfo =
                updateExistMembersAndExtractIncomingMembers(
                        existingMembers, updateMembersInfo, deleteMembersId);
        log.info("StudyTeam update: 신규 멤버 추출 완료 - count={}", incomingMembersInfo.size());

        team.update(studyData, isRecruited);
        log.info("StudyTeam update: 팀 정보 업데이트 완료");

        if (!incomingMembersInfo.isEmpty()) {
            final Map<Long, User> users =
                    userService.getIdAndUserMap(
                            incomingMembersInfo, StudyMemberInfoRequest::getUserId);
            final List<StudyMember> incomingMembers =
                    StudyMemberMapper.toEntities(incomingMembersInfo, team, users);
            team.addMembers(incomingMembers);
            log.info("StudyTeam update: 신규 멤버 추가 완료 - count={}", incomingMembers.size());
        }

        if (!wasRecruit && isRecruited) {
            log.info("StudyTeam update: 모집 상태 false → true 변경 감지 - Slack 알림 응답 반환");
            final List<LeaderInfo> leaders = team.getLeaders();
            return StudyTeamMapper.toUpdatedResponse(studyTeamId, team, leaders);
        }

        log.info("StudyTeam update: 인덱스만 업데이트 후 응답 반환 - studyTeamId={}", studyTeamId);
        return StudyTeamMapper.toIndexOnlyUpdateResponse(studyTeamId, team);
    }

    private void verifyUserIsActiveStudyMember(Long studyTeamId, Long userId) {
        log.info("StudyTeam verifyUserIsActiveStudyMember: 유저 검증 시작 - teamId={}, userId={}", studyTeamId, userId);
        final boolean isMember =
                studyMemberRepository.existsByUserIdAndStudyTeamIdAndIsDeletedFalseAndStatus(
                        userId, studyTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            log.info("StudyTeam verifyUserIsActiveStudyMember: 유저가 승인 멤버가 아님 - teamId={}, userId={}", studyTeamId, userId);
            throw new TeamInvalidActiveRequester();
        }
    }

    private void validateResultImageCount(
            Long studyTeamId, List<String> resultImages, List<Long> deleteImages) {
        log.info("StudyTeam validateResultImageCount: 이미지 개수 검증 시작 - teamId={}, 업로드={}, 삭제={}",
                studyTeamId, resultImages.size(), deleteImages.size());
        final int resultImageCount = studyImageRepository.countByStudyTeamId(studyTeamId);
        if (resultImageCount - deleteImages.size() + resultImages.size() > 10) {
            log.info("StudyTeam validateResultImageCount: 이미지 개수 초과 - 기존={}, 업로드={}, 삭제={}",
                    resultImageCount, resultImages.size(), deleteImages.size());
            throw new StudyExceededResultImageException();
        }
    }

    private List<StudyMemberInfoRequest> updateExistMembersAndExtractIncomingMembers(
            List<StudyMember> existingMembers,
            List<StudyMemberInfoRequest> updateMember,
            List<Long> deleteMemberIds) {
        log.info("StudyTeam updateExistMembersAndExtractIncomingMembers: 멤버 갱신 및 필터 시작 - update={}, delete={}",
                updateMember.size(), deleteMemberIds.size());

        final Map<Long, StudyMemberInfoRequest> updateMap =
                toUserIdAndMemberInfoRequest(updateMember, StudyMemberInfoRequest::getUserId);
        final Set<Long> deleteIdSet = new HashSet<>(deleteMemberIds);
        checkDuplicateUpdateMembers(updateMap, updateMember);
        checkDuplicateDeleteMembers(deleteIdSet, deleteMemberIds);
        applyMemberStateChanges(existingMembers, updateMap, deleteIdSet);

        return extractIncomingMembers(updateMap);
    }

    private void applyMemberStateChanges(
            List<StudyMember> existingMembers,
            Map<Long, StudyMemberInfoRequest> updateMap,
            Set<Long> deleteIdSet) {
        log.info("StudyTeam applyMemberStateChanges: 멤버 상태 변경 시작 - updateMap={}, deleteSet={}",
                updateMap.keySet(), deleteIdSet);

        final Set<Long> toActive = new HashSet<>();
        final Set<Long> toInactive = new HashSet<>();
        final int updateCount = updateMap.size();
        for (StudyMember member : existingMembers) {
            final Long userId = member.getUser().getId();
            final boolean markedForDelete = deleteIdSet.contains(member.getId());
            final StudyMemberInfoRequest updateInfo = updateMap.get(userId);

            if (markedForDelete) {
                if (member.isDeleted()) {
                    log.error("StudyTeam applyMemberStateChanges: 이미 삭제된 멤버 재삭제 시도 - memberId={}", member.getId());
                    throw new StudyMemberNotFoundException();
                }
                if (updateInfo != null) {
                    log.error("StudyTeam applyMemberStateChanges: 동일 멤버에 삭제·수정 중복 요청 - memberId={}", member.getId());
                    throw new TeamDuplicateDeleteUpdateException();
                }
                member.softDelete();
                toInactive.add(member.getId());
                continue;
            }

            if (updateInfo != null) {
                member.toActive(updateInfo.getIsLeader());
                toActive.add(member.getId());
                updateMap.remove(userId);
            }
        }
        final boolean allDeletesProcessed = deleteIdSet.size() == toInactive.size();
        final boolean allUpdatesAccountedFor = toActive.size() + updateMap.size() == updateCount;
        final boolean memberCountConsistent =
                deleteIdSet.size() + toInactive.size() == existingMembers.size();

        if (!allDeletesProcessed || !allUpdatesAccountedFor || !memberCountConsistent) {
            log.error("StudyTeam applyMemberStateChanges: 멤버 상태 변경 일치 실패 - updateMap 남은={}, 삭제된={}, 기존={}",
                    updateMap.size(), toInactive.size(), existingMembers.size());
            throw new TeamMissingUpdateMemberException();
        }
        log.info("StudyTeam applyMemberStateChanges: 멤버 상태 변경 완료 - 활성화={}, 비활성화={}", toActive, toInactive);
    }

    /**
     * 스터디 팀 모집을 종료합니다.
     *
     * @param teamId 팀 ID
     * @param userId 요청자 ID
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws TeamInvalidActiveRequester 요청자가 승인된 멤버가 아닌 경우
     */
    @Transactional
    public void close(Long teamId, Long userId) {
        log.info("StudyTeam close: 모집 마감 요청 시작 - teamId={}, userId={}", teamId, userId);

        boolean isExisted = studyMemberService.checkActiveMemberByTeamAndUser(teamId, userId);
        if (!isExisted) {
            log.error("StudyTeam close: 유저가 승인 멤버 아님 - teamId={}, userId={}", teamId, userId);
            throw new TeamInvalidActiveRequester();
        }
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.warn("StudyTeam close: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        st.close();
    }

    /**
     * 스터디 팀을 soft delete 처리합니다.
     *
     * <p>처리 순서:
     *
     * <ol>
     *   <li>삭제 권한 검증
     *   <li>팀 및 연관 데이터 소프트 삭제
     *   <li>슬랙/검색 인덱스에서 제거
     * </ol>
     *
     * @param teamId 삭제할 팀 ID
     * @param userId 삭제 요청자 ID
     * @throws TeamInvalidActiveRequester 삭제 권한이 없는 경우
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public void softDelete(Long teamId, Long userId) {
        log.info("StudyTeam softDelete: 삭제 요청 시작 - teamId={}, userId={}", teamId, userId);
        verifyUserIsActiveStudyMember(teamId, userId);
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("StudyTeam softDelete: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        st.softDelete();
        log.info("StudyTeam softDelete: 삭제 완료 - teamId={}", teamId);
    }

    /**
     * 사용자가 스터디 팀에 지원합니다.
     *
     * @param request 지원 요청 DTO
     * @param applicantId 지원자 ID
     * @return 슬랙 메시지 전송을 위한 DM 요청 리스트
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws StudyTeamRecruitmentClosedException 팀이 모집 중이 아닌 경우
     */
    @Transactional
    public List<StudySlackRequest.DM> apply(StudyTeamApplyRequest request, Long applicantId) {
        final Long teamId = request.studyTeamId();
        log.info("StudyTeam apply: 지원 요청 시작 - teamId={}, applicantId={}", teamId, applicantId);
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.warn("StudyTeam apply: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        if (!st.isRecruited()) {
            log.warn("StudyTeam apply: 모집 종료된 팀 - teamId={}", teamId);
            throw new StudyTeamRecruitmentClosedException();
        }

        final StudyMember sm = studyMemberService.applyApplicant(st, applicantId, request.summary());
        final String applicantEmail = sm.getUser().getEmail();
        final List<LeaderInfo> leaders = st.getLeaders();
        log.info("StudyTeam apply: 지원 완료 - applicantEmail={}", applicantEmail);
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.PENDING);
    }

    /**
     * 사용자가 스터디 팀 지원을 취소합니다.
     *
     * @param teamId 팀 ID
     * @param applicantId 지원자 ID
     * @return 슬랙 메시지 전송을 위한 DM 요청 리스트
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws StudyMemberNotFoundException 지원 정보가 존재하지 않는 경우
     */
    @Transactional
    public List<StudySlackRequest.DM> cancelApplication(Long teamId, Long applicantId) {
        log.info("StudyTeam cancelApplication: 지원 취소 시작 - teamId={}, applicantId={}", teamId, applicantId);
        final StudyMember sm = studyMemberRepository
                .findByStudyTeamIdAndUserId(teamId, applicantId)
                .orElseThrow(() -> {
                    log.error("StudyTeam cancelApplication: 지원자 없음 - teamId={}, userId={}", teamId, applicantId);
                    return new StudyMemberNotFoundException();
                });
        final String applicantEmail = sm.getUser().getEmail();
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("StudyTeam cancelApplication: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        st.remove(sm);
        final List<LeaderInfo> leaders = st.getLeaders();
        log.info("StudyTeam cancelApplication: 취소 완료 - applicantEmail={}", applicantEmail);
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.CANCELLED);
    }

    /**
     * 스터디 팀의 지원자를 승인합니다.
     *
     * @param teamId 팀 ID
     * @param userId 요청자 ID
     * @param applicantId 승인할 사용자 ID
     * @return 슬랙 메시지 전송을 위한 DM 요청 리스트
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws StudyMemberNotFoundException 지원자가 존재하지 않거나 상태가 올바르지 않은 경우
     * @throws TeamInvalidActiveRequester 요청자가 승인된 멤버가 아닌 경우
     */

    public List<StudySlackRequest.DM> acceptApplicant(Long teamId, Long userId, Long applicantId) {
        log.info("StudyTeam acceptApplicant: 승인 요청 - teamId={}, userId={}, applicantId={}", teamId, userId, applicantId);
        verifyUserIsActiveStudyMember(teamId, userId);
        final String applicantEmail = studyMemberService.acceptApplicant(teamId, applicantId);
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("StudyTeam acceptApplicant: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        final List<LeaderInfo> leaders = st.getLeaders();
        log.info("StudyTeam acceptApplicant: 승인 완료 - applicantEmail={}", applicantEmail);
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.APPROVED);
    }

    /**
     * 스터디 팀의 지원자를 거절합니다.
     *
     * @param teamId 팀 ID
     * @param userId 요청자 ID
     * @param applicantId 거절할 사용자 ID
     * @return 슬랙 메시지 전송을 위한 DM 요청 리스트
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     * @throws StudyMemberNotFoundException 지원자가 존재하지 않거나 상태가 올바르지 않은 경우
     * @throws TeamInvalidActiveRequester 요청자가 승인된 멤버가 아닌 경우
     */
    public List<StudySlackRequest.DM> rejectApplicant(Long teamId, Long userId, Long applicantId) {
        log.info("StudyTeam rejectApplicant: 거절 요청 - teamId={}, userId={}, applicantId={}", teamId, userId, applicantId);
        verifyUserIsActiveStudyMember(teamId, userId);
        final String applicantEmail = studyMemberService.rejectApplicant(teamId, applicantId);
        final StudyTeam st = studyTeamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("StudyTeam rejectApplicant: 팀 없음 - teamId={}", teamId);
                    return new StudyTeamNotFoundException();
                });
        final List<LeaderInfo> leaders = st.getLeaders();
        log.info("StudyTeam rejectApplicant: 거절 완료 - applicantEmail={}", applicantEmail);
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.REJECT);
    }

    /**
     * 스터디 팀의 지원자 목록을 조회합니다. (요청자가 팀의 승인된 멤버인 경우에만 응답)
     *
     * @param studyTeamId 팀 ID
     * @param userId 요청자 ID
     * @return 지원자 응답 리스트, 승인된 멤버가 아니면 빈 리스트 반환
     */
    public List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId) {
        log.info("StudyTeam getApplicants: 지원자 목록 조회 요청 - teamId={}, userId={}", studyTeamId, userId);
        final boolean isActive = studyMemberService.checkActiveMemberByTeamAndUser(studyTeamId, userId);
        if (!isActive) {
            log.info("StudyTeam getApplicants: 승인된 멤버 아님 - userId={}", userId);
            return List.of();
        }
        final List<StudyApplicantResponse> result = studyMemberService.getApplicants(studyTeamId);
        log.info("StudyTeam getApplicants: 조회 완료 - 수={}", result.size());
        return result;
    }

    /**
     * 스터디 팀의 상세 정보를 조회하고, 조회수를 1 증가시킵니다.
     *
     * @param studyTeamId 팀 ID
     * @return 상세 응답 DTO
     * @throws StudyTeamNotFoundException 팀이 존재하지 않는 경우
     */
    @Transactional
    public StudyTeamDetailResponse updateViewCountAndGetDetail(Long studyTeamId) {
        log.info("StudyTeam getDetail: 상세 조회 시작 - teamId={}", studyTeamId);
        final StudyTeam study = studyTeamRepository.findById(studyTeamId)
                .orElseThrow(() -> {
                    log.error("StudyTeam getDetail: 팀 없음 - teamId={}", studyTeamId);
                    return new StudyTeamNotFoundException();
                });
        study.increaseViewCount();
        final List<StudyMember> sm = study.getStudyMembers();
        log.info("StudyTeam getDetail: 조회 완료 - 멤버 수={}", sm.size());
        return StudyTeamMapper.toDetailResponse(study, sm);
    }
}
