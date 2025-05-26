package backend.techeerzip.domain.auth.service;

import backend.techeerzip.domain.auth.dto.request.LoginRequest;
import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.domain.auth.exception.AuthInvalidCredentialsException;
import backend.techeerzip.domain.auth.exception.AuthNotTecheerException;
import backend.techeerzip.domain.auth.exception.AuthNotVerifiedEmailException;
import backend.techeerzip.domain.auth.exception.EmailSendFailedException;
import backend.techeerzip.domain.auth.exception.InvalidVerificationCodeException;
import backend.techeerzip.domain.auth.jwt.JwtTokenProvider;
import backend.techeerzip.domain.auth.util.AuthEmailTemplate;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    @Value("${PROFILE_IMG_URL}")
    private String profileImgUrl;

    @Value("${SLACK}")
    private String slackSecretKey;

    @Value("${EMAIL_USER}")
    private String emailUser;

    // 이메일 인증 코드 전송
    public void sendVerificationEmail(String email) {
        if (!checkTecheer(email)) {
            logger.error("테커 회원이 아닙니다.", CONTEXT);
            throw new AuthNotTecheerException();
        }

        logger.info("이메일 인증 코드 생성 중 - email: {}", email, CONTEXT);
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));
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
    private boolean checkTecheer(String email) {
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
            logger.error("해당 사용자 테커 여부 조회 실패", CONTEXT);
            throw new AuthNotTecheerException();
        }
    }

    // 이메일 인증 코드 확인
    public void verifyCode(String email, String code) {
        String cached = redisTemplate.opsForValue().get(email);

        if (cached == null) {
            throw new AuthNotVerifiedEmailException();
        }

        if (!cached.equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        redisTemplate.delete(email);
        redisTemplate.opsForValue().set("verified_" + email, "true", Duration.ofMinutes(10));
    }

    public boolean checkEmailverified(String email) {
        return "true".equals(redisTemplate.opsForValue().get("verified_" + email));
    }

    public void login(HttpServletResponse response, LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        logger.debug(String.format("로그인 요청 - email: %s", email), CONTEXT);

        try {
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

            ResponseCookie refreshToken =
                    ResponseCookie.from("refresh_token", tokenPair.getRefreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(60 * 60 * 24 * 7)
                            .sameSite("None")
                            .build();

            response.addHeader("Set-Cookie", accessToken.toString());
            response.addHeader("Set-Cookie", refreshToken.toString());

        } catch (Exception e) {
            logger.warn(String.format("로그인 실패 - email: %s", email), CONTEXT);
            throw new AuthInvalidCredentialsException();
        }
    }
}
