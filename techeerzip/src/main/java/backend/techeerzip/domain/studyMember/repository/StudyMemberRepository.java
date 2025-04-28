package backend.techeerzip.domain.studyMember.repository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyTeamId(Long studyTeamId);
    List<StudyMember> findByUserId(Long userId);
} 