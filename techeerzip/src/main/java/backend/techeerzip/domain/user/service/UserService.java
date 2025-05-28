package backend.techeerzip.domain.user.service;

import java.util.List;
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
import backend.techeerzip.domain.role.entity.Role;
import backend.techeerzip.domain.role.exception.RoleNotFoundException;
import backend.techeerzip.domain.role.repository.RoleRepository;
import backend.techeerzip.domain.user.dto.response.GetPermissionResponse;
import backend.techeerzip.domain.user.dto.response.GetProfileImgResponse;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.PermissionRequest;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.exception.UserProfileImgFailException;
import backend.techeerzip.domain.user.exception.UserUnauthorizedAdminException;
import backend.techeerzip.domain.user.mapper.UserMapper;
import backend.techeerzip.domain.user.repository.PermissionRequestRepository;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Value("${PROFILE_IMG_URL}")
    private String profileImgUrl;

    @Value("${SLACK}")
    private String slackSecretKey;

    private final RestTemplate restTemplate;
    private final AuthService authService;
    //    private final ResumeService resumeService;
    //    private final IndexService indexService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PermissionRequestRepository permissionRequestRepository;
    private final RoleRepository roleRepository;
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

    public PermissionRequest createUserPermissionRequest(Long userId, Long roleId) {
        logger.info("권한 요청 - userId: {} newRoleId: - {}", CONTEXT);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        PermissionRequest permissionRequest =
                PermissionRequest.builder().user(user).requestedRoleId(roleId).build();

        return permissionRequestRepository.save(permissionRequest);
    }

    @Transactional
    public List<GetPermissionResponse> getAllPendingPermissionRequests(Long currentUserId) {
        User currentUser =
                userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        Long roleId = currentUser.getRole().getId();
        if (roleId != 1) {
            logger.warn("권한 없음 - userId: {}", currentUserId);
            throw new UserUnauthorizedAdminException();
        }

        logger.info("권한 요청 목록 조회", CONTEXT);

        return permissionRequestRepository.findByStatus(StatusCategory.PENDING).stream()
                .map(
                        pr ->
                                GetPermissionResponse.builder()
                                        .id(pr.getId())
                                        .userId(pr.getUser().getId())
                                        .name(pr.getUser().getName())
                                        .requestedRoleId(pr.getRequestedRoleId())
                                        .status(pr.getStatus())
                                        .createdAt(pr.getCreatedAt())
                                        .build())
                .toList();
    }

    @Transactional
    public void approveUserPermission(Long currentUserId, Long userId, Long newRoleId) {
        User currentUser =
                userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        Long roleId = currentUser.getRole().getId();
        if (roleId != 1) {
            logger.warn("권한 없음 - userId: {}", currentUserId);
            throw new UserUnauthorizedAdminException();
        }

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Role newRole = roleRepository.findById(newRoleId).orElseThrow(RoleNotFoundException::new);
        user.setRole(newRole);

        userRepository.save(user);

        logger.info("사용자 권한 요청 승인 완료 - userId:{}", userId, CONTEXT);

        permissionRequestRepository.updateStatusByUserId(userId, StatusCategory.APPROVED);
    }
}
