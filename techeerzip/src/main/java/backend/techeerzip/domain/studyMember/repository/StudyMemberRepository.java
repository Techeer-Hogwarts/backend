package backend.techeerzip.domain.studyMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    List<StudyMember> findByStudyTeamId(Long studyTeamId);

    List<StudyMember> findByUserId(Long userId);
}
