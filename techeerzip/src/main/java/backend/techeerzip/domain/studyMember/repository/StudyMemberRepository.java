package backend.techeerzip.domain.studyMember.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.global.entity.StatusCategory;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyTeamId(Long studyTeamId);

    List<StudyMember> findByUserId(Long userId);

    boolean existsByUserIdAndStudyTeamIdAndIsDeletedFalseAndStatus(
            Long userId, Long studyTeamId, StatusCategory statusCategory);

    List<StudyMember> findAllByStudyTeamId(Long studyTeamId);

    boolean existsByStudyTeamIdAndUserId(Long studyTeamId, Long userId);

    Optional<StudyMember> findByStudyTeamIdAndUserId(Long id, Long applicantId);

    Optional<StudyMember> findByStudyTeamIdAndUserIdAndStatus(
            Long teamId, Long applicantId, StatusCategory statusCategory);
}
