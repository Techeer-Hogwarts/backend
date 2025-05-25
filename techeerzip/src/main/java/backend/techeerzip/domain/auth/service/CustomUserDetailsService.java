package backend.techeerzip.domain.auth.service;

import backend.techeerzip.domain.auth.jwt.CustomUserPrincipal;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomLogger logger;

    private static final String CONTEXT = "CustomUserDetailsService";

    @Value("${swagger.username}")
    private String swaggerUsername;

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Swagger 사용자
        if (email.equals(swaggerUsername)) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(swaggerUsername)
                    .password(passwordEncoder.encode(swaggerPassword))
                    .roles("USER")
                    .build();
        }

        // JWT 인증 사용자
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn(String.format("사용자 조회 실패 - email: %s", email), CONTEXT);
                    return new UserNotFoundException();
                });

        logger.debug(String.format("사용자 조회 성공 - userId: %d, email: %s", user.getId(), user.getEmail()), CONTEXT);

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());

        return new CustomUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }
}