package backend.techeerzip.domain.studyTeam.service;

import static backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepositoryImpl.ensureMaxSize;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.*;
import static backend.techeerzip.domain.projectTeam.service.ProjectTeamService.getNextInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.exception.TeamInvalidActiveRequester;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.exception.TeamDuplicateDeleteUpdateException;
import backend.techeerzip.domain.projectTeam.exception.TeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.mapper.TeamIndexMapper;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyMember.StudyMemberMapper;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyMember.exception.StudyMemberNotFoundException;
import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import backend.techeerzip.domain.studyMember.service.StudyMemberService;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.dto.request.StudyData;
import backend.techeerzip.domain.studyTeam.dto.request.StudyMemberInfoRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamCreateResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamUpdateResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.exception.StudyExceededResultImageException;
import backend.techeerzip.domain.studyTeam.exception.StudyTeamDuplicateException;
import backend.techeerzip.domain.studyTeam.exception.StudyTeamInvalidRecruitNumException;
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

    @Transactional
    public StudyTeamCreateResponse create(List<String> resultUrls, StudyTeamCreateRequest request) {
        final StudyData studyData = request.getStudyData();
        final List<StudyMemberInfoRequest> membersInfo = request.getStudyMember();

        checkUniqueStudyName(studyData.getName());
        boolean isRecruited = checkRecruit(studyData.getIsRecruited(), studyData.getRecruitNum());
        validateLeaderExists(membersInfo, StudyMemberInfoRequest::getIsLeader);
        final Map<Long, User> users =
                userService.getIdAndUserMap(membersInfo, StudyMemberInfoRequest::getUserId);
        final StudyTeam team =
                studyTeamRepository.save(StudyTeamMapper.toEntity(studyData, isRecruited));
        if (!resultUrls.isEmpty()) {
            studyImageRepository.saveAll(StudyImageMapper.toManyResultEntity(resultUrls, team));
        }
        final List<StudyMember> memberEntities =
                membersInfo.stream()
                        .map(
                                info ->
                                        StudyMemberMapper.toEntity(
                                                info, team, users.get(info.getUserId())))
                        .toList();
        final List<StudyMember> members = studyMemberRepository.saveAll(memberEntities);
        final List<LeaderInfo> leaders = extractLeaders(members);

        return new StudyTeamCreateResponse(
                team.getId(),
                StudySlackMapper.toChannelRequest(team, leaders),
                TeamIndexMapper.toStudyRequest(team));
    }

    private static List<LeaderInfo> extractLeaders(List<StudyMember> members) {
        return members.stream()
                .filter(StudyMember::isLeader)
                .map(m -> new LeaderInfo(m.getUser().getName(), m.getUser().getName()))
                .toList();
    }

    private static boolean checkRecruit(Boolean isRecruited, Integer recruitNum) {
        if (recruitNum < 0) {
            throw new StudyTeamInvalidRecruitNumException();
        }
        if (recruitNum == 0) {
            return false;
        }
        return isRecruited;
    }

    private void checkUniqueStudyName(String name) {
        final boolean exist = studyTeamRepository.existsByName(name);
        if (exist) {
            throw new StudyTeamDuplicateException();
        }
    }

    public GetAllTeamsResponse getSliceTeams(GetStudyTeamsQuery query) {
        final int limit = query.getLimit();
        final SortType sortType = query.getSortType();
        final List<StudyTeam> teams = studyTeamDslRepository.sliceTeams(query);
        final SliceNextCursor next = setNextInfo(teams, limit, sortType);
        final List<StudyTeam> fitTeams = ensureMaxSize(teams, limit);
        final List<SliceTeamsResponse> responses =
                new ArrayList<>(fitTeams.stream().map(StudyTeamMapper::toGetAllResponse).toList());
        return new GetAllTeamsResponse(responses, next);
    }

    public List<StudySliceTeamsResponse> getYoungTeamsById(List<Long> keys) {
        final List<StudyTeam> teams = studyTeamRepository.findAllById(keys);
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }

    private static SliceNextCursor setNextInfo(
            List<StudyTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            return SliceNextCursor.builder().hasNext(false).build();
        }
        final StudyTeam last = sortedTeams.getLast();
        return getNextInfo(
                sortType,
                last.getId(),
                last.getUpdatedAt(),
                last.getViewCount(),
                last.getLikeCount());
    }

    @Transactional
    public StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<String> resultImageUrls,
            StudyTeamUpdateRequest request) {
        final List<StudyMemberInfoRequest> updateMembersInfo = request.getStudyMember();
        final StudyData studyData = request.getStudyData();
        final List<Long> deleteResultImages = request.getDeleteImages();
        final List<Long> deleteMembersId = request.getDeleteMembers();
        verifyUserIsActiveStudyMember(studyTeamId, userId);
        validateLeaderExists(request.getStudyMember(), StudyMemberInfoRequest::getIsLeader);
        validateResultImageCount(studyTeamId, resultImageUrls, deleteMembersId);

        final StudyTeam team =
                studyTeamRepository
                        .findById(studyTeamId)
                        .orElseThrow(StudyTeamNotFoundException::new);
        final boolean wasRecruit = team.getIsRecruited();
        final boolean isRecruited = checkRecruit(team.getIsRecruited(), studyData.getRecruitNum());

        if (!team.isSameName(studyData.getName())) {
            checkUniqueStudyName(studyData.getName());
        }

        if (!deleteResultImages.isEmpty()) {
            studyImageRepository.deleteAllById(deleteResultImages);
        }

        if (!resultImageUrls.isEmpty()) {
            final List<StudyResultImage> images =
                    StudyImageMapper.toManyResultEntity(resultImageUrls, team);
            team.addResultImage(images);
        }

        final List<StudyMember> existingMembers =
                studyMemberRepository.findAllByStudyTeamId(studyTeamId);
        final List<StudyMemberInfoRequest> incomingMembersInfo =
                updateExistMembersAndExtractIncomingMembers(
                        existingMembers, updateMembersInfo, deleteMembersId);
        team.update(studyData, isRecruited);
        if (!incomingMembersInfo.isEmpty()) {
            final Map<Long, User> users =
                    userService.getIdAndUserMap(
                            incomingMembersInfo, StudyMemberInfoRequest::getUserId);
            final List<StudyMember> incomingMembers =
                    StudyMemberMapper.toEntities(incomingMembersInfo, team, users);
            team.addMembers(incomingMembers);
        }
        /* 12.isRecruited 값이 false → true 로 변경되었을 때 Slack 알림 전송 **/
        if (!wasRecruit && isRecruited) {
            // send Slack
            final List<LeaderInfo> leaders = team.getLeaders();
            return StudyTeamMapper.toUpdatedResponse(studyTeamId, team, leaders);
        }

        /* 13. 인덱스 업데이트 */
        return StudyTeamMapper.toIndexOnlyUpdateResponse(studyTeamId, team);
    }

    private void verifyUserIsActiveStudyMember(Long studyTeamId, Long userId) {
        final boolean isMember =
                studyMemberRepository.existsByUserIdAndStudyTeamIdAndIsDeletedFalseAndStatus(
                        userId, studyTeamId, StatusCategory.APPROVED);
        if (!isMember) {
            throw new TeamInvalidActiveRequester();
        }
    }

    private void validateResultImageCount(
            Long studyTeamId, List<String> resultImages, List<Long> deleteImages) {
        final int resultImageCount = studyImageRepository.countByStudyTeamId(studyTeamId);
        if (resultImageCount - deleteImages.size() + resultImages.size() > 10) {
            throw new StudyExceededResultImageException();
        }
    }

    private List<StudyMemberInfoRequest> updateExistMembersAndExtractIncomingMembers(
            List<StudyMember> existingMembers,
            List<StudyMemberInfoRequest> updateMember,
            List<Long> deleteMemberIds) {
        // 요청 중 수정하려는 유저 ID → 요청 정보 Map
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
        final Set<Long> toActive = new HashSet<>();
        final Set<Long> toInactive = new HashSet<>();
        final int updateCount = updateMap.size();
        for (StudyMember member : existingMembers) {
            final Long userId = member.getUser().getId();
            final boolean markedForDelete = deleteIdSet.contains(member.getId());
            final StudyMemberInfoRequest updateInfo = updateMap.get(userId);

            if (markedForDelete) {
                if (member.isDeleted()) {
                    throw new StudyMemberNotFoundException();
                }
                if (updateInfo != null) {
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
        if (deleteIdSet.size() != toInactive.size()
                || toActive.size() + updateMap.size() != updateCount
                || deleteIdSet.size() + toInactive.size() != existingMembers.size()) {
            throw new TeamMissingUpdateMemberException();
        }
    }

    @Transactional
    public void close(Long teamId, Long userId) {
        boolean isExisted = studyMemberService.checkActiveMemberByTeamAndUser(teamId, userId);
        if (!isExisted) {
            throw new TeamInvalidActiveRequester();
        }
        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        st.close();
    }

    @Transactional
    public void softDelete(Long teamId, Long userId) {
        verifyUserIsActiveStudyMember(teamId, userId);
        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        st.softDelete();
    }

    @Transactional
    public List<StudySlackRequest.DM> apply(StudyTeamApplyRequest request, Long applicantId) {
        final Long teamId = request.studyTeamId();
        final String summary = request.summary();

        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        if (!st.isRecruited()) {
            throw new StudyTeamRecruitmentClosedException();
        }

        final StudyMember sm = studyMemberService.applyApplicant(st, applicantId, summary);
        final List<LeaderInfo> leaders = st.getLeaders();
        final String applicantEmail = sm.getUser().getEmail();

        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.PENDING);
    }

    @Transactional
    public List<StudySlackRequest.DM> cancelApplication(Long teamId, Long applicantId) {
        final StudyMember sm =
                studyMemberRepository
                        .findByStudyTeamIdAndUserId(teamId, applicantId)
                        .orElseThrow(StudyMemberNotFoundException::new);
        final String applicantEmail = sm.getUser().getEmail();
        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        st.remove(sm);
        final List<LeaderInfo> leaders = st.getLeaders();
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.CANCELLED);
    }

    public List<StudySlackRequest.DM> acceptApplicant(Long teamId, Long userId, Long applicantId) {
        verifyUserIsActiveStudyMember(teamId, userId);
        final String applicantEmail = studyMemberService.acceptApplicant(teamId, applicantId);
        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        final List<LeaderInfo> leaders = st.getLeaders();
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.APPROVED);
    }

    public List<StudySlackRequest.DM> rejectApplicant(Long teamId, Long userId, Long applicantId) {
        verifyUserIsActiveStudyMember(teamId, userId);
        final String applicantEmail = studyMemberService.rejectApplicant(teamId, applicantId);
        final StudyTeam st =
                studyTeamRepository.findById(teamId).orElseThrow(StudyTeamNotFoundException::new);
        final List<LeaderInfo> leaders = st.getLeaders();
        return StudySlackMapper.toDmRequest(st, leaders, applicantEmail, StatusCategory.REJECT);
    }

    public List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId) {
        final boolean isActive =
                studyMemberService.checkActiveMemberByTeamAndUser(studyTeamId, userId);
        if (!isActive) {
            return List.of();
        }
        return studyMemberService.getApplicants(studyTeamId);
    }

    @Transactional
    public StudyTeamDetailResponse updateViewCountAndGetDetail(Long studyTeamId) {
        final StudyTeam study =
                studyTeamRepository
                        .findById(studyTeamId)
                        .orElseThrow(StudyTeamNotFoundException::new);
        study.increaseViewCount();
        final List<StudyMember> sm = study.getStudyMembers();
        return StudyTeamMapper.toDetailResponse(study, sm);
    }
}
