package backend.techeerzip.domain.user.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import backend.techeerzip.domain.auth.service.AuthService;
import backend.techeerzip.domain.user.dto.response.GetProfileImgResponse;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.exception.UserProfileImgFailException;
import backend.techeerzip.domain.user.mapper.UserMapper;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RestTemplate restTemplate;

    @Value("${PROFILE_IMG_URL}")
    private String profileImgUrl;

    @Value("${SLACK}")
    private String slackSecretKey;

    private final AuthService authService;
    //    private final ResumeService resumeService;
    //    private final IndexService indexService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserService";

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        authService.verifyCode(email, code);

        User user =
                userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional(readOnly = true)
    public GetUserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return userMapper.toGetUserResponse(user);
    }

    @Transactional(readOnly = true)
    public GetProfileImgResponse updateProfileImg(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        String latestProfileImg = fetchProfileImg(user.getEmail());
        user.setProfileImage(latestProfileImg);

        return new GetProfileImgResponse(user.getProfileImage());
    }

    private String fetchProfileImg(String email) {
        String url = profileImgUrl;
        String secret = slackSecretKey;

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("email", email, "secret", secret);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (response.getStatusCode().is2xxSuccessful()
                    && responseBody != null
                    && responseBody.containsKey("image")) {
                return (String) responseBody.get("image");
            }

            logger.error("슬랙 이미지 요청 실패 - email: {}", email, CONTEXT);
            throw new UserProfileImgFailException();
        } catch (Exception e) {
            logger.error("슬랙 이미지 요청 실패 - email: {}", email, CONTEXT);
            throw new UserProfileImgFailException();
        }
    }
}
