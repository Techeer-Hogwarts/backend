package backend.techeerzip.domain.studyMember.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectMember.exception.TeamMemberNotFoundException;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyMember.repository.StudyMemberDslRepository;
import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;
    private final StudyMemberDslRepository studyMemberDslRepository;
    private final UserRepository userRepository;

    public boolean checkActiveMemberByTeamAndUser(Long studyTeamId, Long userId) {
        return studyMemberRepository.existsByStudyTeamIdAndUserId(studyTeamId, userId);
    }

    public StudyMember applyApplicant(StudyTeam team, Long applicantId, String summary) {
        final StudyMember st =
                studyMemberRepository
                        .findByStudyTeamIdAndUserId(team.getId(), applicantId)
                        .orElseGet(() -> createApplicant(team, applicantId, summary));

        if (!st.isPending()) {
            st.toApplicant(); // 상태 변경이 필요한 경우에만 수행
        }
        return st;
    }

    private StudyMember createApplicant(StudyTeam team, Long applicantId, String summary) {
        final StudyMember pm =
                StudyMember.builder()
                        .status(StatusCategory.PENDING)
                        .summary(summary)
                        .isLeader(false)
                        .studyTeam(team)
                        .user(userRepository.getReferenceById(applicantId))
                        .build();
        return studyMemberRepository.save(pm);
    }

    public String acceptApplicant(Long teamId, Long applicantId) {
        final StudyMember sm =
                studyMemberRepository
                        .findByStudyTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(TeamMemberNotFoundException::new);
        sm.toActive();
        return sm.getUser().getEmail();
    }

    public String rejectApplicant(Long teamId, Long applicantId) {
        final StudyMember sm =
                studyMemberRepository
                        .findByStudyTeamIdAndUserIdAndStatus(
                                teamId, applicantId, StatusCategory.PENDING)
                        .orElseThrow(TeamMemberNotFoundException::new);
        sm.toReject();
        return sm.getUser().getEmail();
    }

    public List<StudyApplicantResponse> getApplicants(Long studyTeamId) {
        return studyMemberDslRepository.findManyApplicants(studyTeamId);
    }
}
