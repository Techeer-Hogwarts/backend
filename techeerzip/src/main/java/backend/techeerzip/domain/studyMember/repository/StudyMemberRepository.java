package backend.techeerzip.domain.studyMember.repository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.global.entity.StatusCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    boolean existsByUserIdAndStudyTeamIdAndIsDeletedFalseAndStatus(
            Long userId, Long studyTeamId, StatusCategory statusCategory);

    List<StudyMember> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE StudyMember sm SET sm.isDeleted = true WHERE sm.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);

    List<StudyMember> findAllByStudyTeamId(Long studyTeamId);

    Optional<StudyMember> findByStudyTeamIdAndUserId(Long id, Long applicantId);

    Optional<StudyMember> findByStudyTeamIdAndUserIdAndStatus(
            Long teamId, Long applicantId, StatusCategory statusCategory);

}
