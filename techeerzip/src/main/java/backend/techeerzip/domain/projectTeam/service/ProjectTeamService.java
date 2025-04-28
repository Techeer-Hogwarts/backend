package backend.techeerzip.domain.projectTeam.service;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTeamService {
    private final ProjectTeamRepository projectTeamRepository;

    // TODO: 필요한 서비스 메서드 구현
} 