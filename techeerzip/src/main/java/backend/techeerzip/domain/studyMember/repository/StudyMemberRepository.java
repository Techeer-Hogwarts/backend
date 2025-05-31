package backend.techeerzip.domain.studyMember.repository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyTeamId(Long studyTeamId);

    List<StudyMember> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.isDeleted = true WHERE s.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
