package backend.techeerzip.domain.studyTeam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;

public interface StudyImageRepository extends JpaRepository<StudyResultImage, Long> {

    int countByStudyTeamId(Long studyTeamId);
}
