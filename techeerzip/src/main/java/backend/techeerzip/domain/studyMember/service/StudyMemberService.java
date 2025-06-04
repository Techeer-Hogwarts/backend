package backend.techeerzip.domain.studyMember.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyMember.exception.StudyMemberNotFoundException;
import backend.techeerzip.domain.studyMember.repository.StudyMemberDslRepository;
import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;
    private final StudyMemberDslRepository studyMemberDslRepository;
    private final UserRepository userRepository;

    public boolean checkActiveMemberByTeamAndUser(Long studyTeamId, Long userId) {
        log.info(
                "StudyMemberService checkActiveMemberByTeamAndUser: teamId={}, userId={}",
                studyTeamId,
                userId);
        final boolean exists =
                studyMemberRepository.existsByUserIdAndStudyTeamIdAndIsDeletedFalseAndStatus(
                        userId, studyTeamId, StatusCategory.APPROVED);
        log.info("StudyMemberService checkActiveMemberByTeamAndUser: 존재 여부={}", exists);
        return exists;
    }

    /**
     * 주어진 팀과 유저 ID에 대해 지원자를 등록하거나, 기존 지원자의 상태를 갱신합니다.
     *
     * <p>기존 지원자가 존재하지 않으면 새로운 지원자를 생성하고, 이미 존재하지만 상태가 PENDING이 아니라면 상태를 PENDING으로 갱신합니다.
     *
     * @param team 스터디 팀 엔티티
     * @param applicantId 지원자 유저 ID
     * @param summary 자기소개 요약
     * @return StudyMember 지원자 엔티티
     */
    @Transactional
    public StudyMember applyApplicant(StudyTeam team, Long applicantId, String summary) {
        log.info(
                "StudyMemberService applyApplicant: teamId={}, applicantId={}, summary={}",
                team.getId(),
                applicantId,
                summary);

        final StudyMember st =
                studyMemberRepository
                        .findByStudyTeamIdAndUserId(team.getId(), applicantId)
                        .orElseGet(
                                () -> {
                                    log.info("StudyMemberService applyApplicant: 기존 지원자 없음, 새로 생성");
                                    return createApplicant(team, applicantId, summary);
                                });

        if (!st.isPending()) {
            log.info("StudyMemberService applyApplicant: 상태가 PENDING이 아님 → 상태를 지원자로 변경");
            st.toApplicant();
        }

        return st;
    }

    /**
     * 새로운 지원자를 생성하여 저장합니다.
     *
     * @param team 스터디 팀 엔티티
     * @param applicantId 지원자 유저 ID
     * @param summary 자기소개 요약
     * @return 생성된 StudyMember 엔티티
     */
    private StudyMember createApplicant(StudyTeam team, Long applicantId, String summary) {
        log.info(
                "StudyMemberService createApplicant: 팀={}, 유저={}, 요약={}",
                team.getId(),
                applicantId,
                summary);

        final StudyMember sm =
                StudyMember.builder()
                        .status(StatusCategory.PENDING)
                        .summary(summary)
                        .isLeader(false)
                        .studyTeam(team)
                        .user(userRepository.getReferenceById(applicantId))
                        .build();

        final StudyMember saved = studyMemberRepository.save(sm);
        log.info("StudyMemberService createApplicant: 저장 완료, memberId={}", saved.getId());
        return saved;
    }

    /**
     * 특정 지원자를 승인(PENDING → APPROVED) 상태로 변경합니다.
     *
     * @param teamId 스터디 팀 ID
     * @param applicantId 승인할 지원자 ID
     * @return 승인된 사용자의 이메일
     * @throws StudyMemberNotFoundException 해당 ID의 PENDING 지원자가 존재하지 않을 경우
     */
    @Transactional
    public String acceptApplicant(Long teamId, Long applicantId) {
        log.info(
                "StudyMemberService acceptApplicant: teamId={}, applicantId={}",
                teamId,
                applicantId);

        final StudyMember sm =
                studyMemberRepository
                        .findByStudyTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(
                                () -> {
                                    log.error("StudyMemberService acceptApplicant: 지원자 없음 → 예외 발생");
                                    return new StudyMemberNotFoundException();
                                });

        sm.toActive();
        log.info(
                "StudyMemberService acceptApplicant: 상태를 APPROVED로 변경, userEmail={}",
                sm.getUser().getEmail());
        return sm.getUser().getEmail();
    }

    /**
     * 특정 지원자를 거절 처리합니다 (상태 변경 또는 삭제).
     *
     * @param teamId 스터디 팀 ID
     * @param applicantId 거절할 지원자 ID
     * @return 거절된 사용자의 이메일
     * @throws StudyMemberNotFoundException 해당 ID의 PENDING 지원자가 존재하지 않을 경우
     */
    @Transactional
    public String rejectApplicant(Long teamId, Long applicantId) {
        log.info(
                "StudyMemberService rejectApplicant: teamId={}, applicantId={}",
                teamId,
                applicantId);

        final StudyMember sm =
                studyMemberRepository
                        .findByStudyTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(
                                () -> {
                                    log.error("StudyMemberService rejectApplicant: 지원자 없음 → 예외 발생");
                                    return new StudyMemberNotFoundException();
                                });

        sm.toReject();
        log.info(
                "StudyMemberService rejectApplicant: 상태를 거절로 변경, userEmail={}",
                sm.getUser().getEmail());
        return sm.getUser().getEmail();
    }

    /**
     * 주어진 스터디 팀의 모든 지원자(PENDING 상태)를 조회합니다.
     *
     * @param studyTeamId 스터디 팀 ID
     * @return 지원자 응답 리스트
     */
    public List<StudyApplicantResponse> getApplicants(Long studyTeamId) {
        log.info("StudyMemberService getApplicants: teamId={}", studyTeamId);
        List<StudyApplicantResponse> result =
                studyMemberDslRepository.findManyApplicants(studyTeamId);
        log.info("StudyMemberService getApplicants: 응답 개수={}", result.size());
        return result;
    }
}
