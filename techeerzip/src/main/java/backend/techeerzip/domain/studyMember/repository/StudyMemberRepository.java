package backend.techeerzip.domain.studyMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.studyMember.entity.StudyMember;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyTeamId(Long studyTeamId);

    List<StudyMember> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE StudyMember sm SET sm.isDeleted = true WHERE sm.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
