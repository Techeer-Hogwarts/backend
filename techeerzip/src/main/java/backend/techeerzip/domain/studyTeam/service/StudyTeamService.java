package backend.techeerzip.domain.studyTeam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.studyTeam.repository.StudyTeamRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyTeamService {
    private final StudyTeamRepository studyTeamRepository;

    // TODO: 필요한 서비스 메서드 구현
}
