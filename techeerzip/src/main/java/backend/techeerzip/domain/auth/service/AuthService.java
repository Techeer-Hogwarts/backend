package backend.techeerzip.domain.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.domain.auth.exception.AuthInvalidCredentialsException;
import backend.techeerzip.domain.auth.exception.AuthNotTecheerException;
import backend.techeerzip.domain.auth.exception.AuthNotVerifiedEmailException;
import backend.techeerzip.domain.auth.exception.EmailSendFailedException;
import backend.techeerzip.domain.auth.exception.InvalidVerificationCodeException;
import backend.techeerzip.domain.auth.jwt.JwtTokenProvider;
import backend.techeerzip.domain.auth.util.AuthEmailTemplate;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSenderImpl mailSender;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomLogger logger;
    private static final String CONTEXT = "AuthService";
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    @Value("${PROFILE_IMG_URL}")
    private String profileImgUrl;

    @Value("${SLACK}")
    private String slackSecretKey;

    @Value("${EMAIL_USER}")
    private String emailUser;

    // 이메일 인증 코드 전송
    public void sendVerificationEmail(String email, Boolean techeer) {

        if (techeer) {
            checkTecheer(email);
        }

        String code = String.format("%06d", secureRandom.nextInt(1000000));
        logger.info("이메일 인증 코드 생성 완료 - email: {}", email, CONTEXT);

        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));
        logger.info("redis 인증 코드 저장 완료 - email: {}", email, CONTEXT);

        sendMail(email, code);
    }

    private void sendMail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailUser);
            helper.setTo(email);
            helper.setSubject("이메일 인증 코드");

            String emailBody = AuthEmailTemplate.build(code);
            helper.setText(emailBody, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            logger.error("이메일 인증 코드 전송 실패 - email:{}", email, CONTEXT);
            throw new EmailSendFailedException();
        }
    }

    // 테커 여부 확인
    public boolean checkTecheer(String email) {
        String url = profileImgUrl;
        String secret = slackSecretKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("email", email, "secret", secret);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return Boolean.TRUE.equals(response.getBody().get("isTecheer"));
        } catch (Exception e) {
            logger.warn("테커 회원이 아닙니다. - email: {}", email, CONTEXT);
            throw new AuthNotTecheerException();
        }
    }

    // 이메일 인증 코드 확인
    public void verifyCode(String email, String code) {
        String cached = redisTemplate.opsForValue().get(email);

        if (cached == null) {
            logger.warn("이메일 인증 필요 - email: {}", email, CONTEXT);
            throw new AuthNotVerifiedEmailException();
        }

        if (!cached.equals(code)) {
            logger.warn("이메일 인증 코드 불일치 - email: {}", email, CONTEXT);
            throw new InvalidVerificationCodeException();
        }

        redisTemplate.delete(email);
        redisTemplate.opsForValue().set("verified_" + email, "true", Duration.ofMinutes(10));
    }

    public boolean checkEmailVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get("verified_" + email));
    }

    public TokenPair login(HttpServletResponse response, LoginRequest loginRequest) {
        String email = loginRequest.getEmail();

        try {
            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow(AuthInvalidCredentialsException::new);

            logger.info("권한 확인 - roleName: {}", user.getRole().getName(), CONTEXT);

            if (user.isDeleted()) {
                throw new AuthInvalidCredentialsException();
            }

            Authentication authentication =
                    authenticationManager.authenticate(loginRequest.toAuthentication());

            TokenPair tokenPair = jwtTokenProvider.generateTokenPair(authentication);

            ResponseCookie accessToken =
                    ResponseCookie.from("access_token", tokenPair.getAccessToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60 * 60)
                            .sameSite("None")
                            .build();

            logger.info("액세스 토큰 생성 완료 - email: {}", email, CONTEXT);

            ResponseCookie refreshToken =
                    ResponseCookie.from("refresh_token", tokenPair.getRefreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60 * 60 * 24 * 7)
                            .sameSite("None")
                            .build();

            logger.info("리프레시 토큰 생성 완료 - email: {}", email, CONTEXT);

            response.addHeader("Set-Cookie", accessToken.toString());
            response.addHeader("Set-Cookie", refreshToken.toString());

            return new TokenPair(tokenPair.getAccessToken(), tokenPair.getRefreshToken());

        } catch (Exception e) {
            logger.warn(String.format("로그인 실패 - email: %s", email), CONTEXT);
            throw new AuthInvalidCredentialsException();
        }
    }

    public void logout(Long userId, HttpServletResponse response) {
        logger.info("로그아웃 요청 - userId:{}", userId, CONTEXT);

        ResponseCookie expiredAccessToken =
                ResponseCookie.from("access_token", "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .sameSite("None")
                        .build();

        ResponseCookie expiredRefreshToken =
                ResponseCookie.from("refresh_token", "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .sameSite("None")
                        .build();

        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());

        logger.info("토큰 무효화 및 쿠키 삭제 완료 - userId: {}", userId, CONTEXT);
    }
}
