package backend.techeerzip.domain.auth.service;

import backend.techeerzip.domain.auth.entity.Auth;
import backend.techeerzip.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final AuthRepository authRepository;

    // TODO: 필요한 서비스 메서드 구현
} 