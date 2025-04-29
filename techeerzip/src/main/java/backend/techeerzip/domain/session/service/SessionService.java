package backend.techeerzip.domain.session.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {
    private final SessionRepository sessionRepository;

    // TODO: 필요한 서비스 메서드 구현
}
