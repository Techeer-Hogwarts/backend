package backend.techeerzip.domain.projectTeam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTeamService {
    private final ProjectTeamRepository projectTeamRepository;

    // TODO: 필요한 서비스 메서드 구현
}
