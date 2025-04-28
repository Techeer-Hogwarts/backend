package backend.techeerzip.domain.studyTeam.repository;

import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyTeamRepository extends JpaRepository<StudyTeam, Long> {
    List<StudyTeam> findAllByOrderByStartDateDesc();
} 