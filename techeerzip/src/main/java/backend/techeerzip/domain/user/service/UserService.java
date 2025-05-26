package backend.techeerzip.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.auth.service.AuthService;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthService authService;
    //    private final ResumeService resumeService;
    //    private final IndexService indexService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserService";

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        authService.verifyCode(email, code);

        User user =
                userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
