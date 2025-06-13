package backend.techeerzip.domain.user.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.auth.exception.AuthNotTecheerException;
import backend.techeerzip.domain.auth.exception.AuthNotVerifiedEmailException;
import backend.techeerzip.domain.auth.service.AuthService;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.bookmark.repository.BookmarkRepository;
import backend.techeerzip.domain.event.repository.EventRepository;
import backend.techeerzip.domain.like.repository.LikeRepository;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.resume.dto.request.CreateResumeRequest;
import backend.techeerzip.domain.resume.repository.ResumeRepository;
import backend.techeerzip.domain.resume.service.ResumeService;
import backend.techeerzip.domain.role.entity.Role;
import backend.techeerzip.domain.role.exception.RoleNotFoundException;
import backend.techeerzip.domain.role.repository.RoleRepository;
import backend.techeerzip.domain.session.repository.SessionRepository;
import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import backend.techeerzip.domain.task.service.TaskService;
import backend.techeerzip.domain.user.dto.request.CreateExternalUserRequest;
import backend.techeerzip.domain.user.dto.request.CreateUserRequest;
import backend.techeerzip.domain.user.dto.request.CreateUserWithResumeRequest;
import backend.techeerzip.domain.user.dto.request.GetUserProfileListRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserInfoRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserWithExperienceRequest;
import backend.techeerzip.domain.user.dto.response.GetPermissionResponse;
import backend.techeerzip.domain.user.dto.response.GetProfileImgResponse;
import backend.techeerzip.domain.user.dto.response.GetUserProfileListResponse;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.entity.BootcampPeriod;
import backend.techeerzip.domain.user.entity.JoinReason;
import backend.techeerzip.domain.user.entity.PermissionRequest;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserAlreadyExistsException;
import backend.techeerzip.domain.user.exception.UserNotBootcampPeriodException;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.exception.UserNotResumeException;
import backend.techeerzip.domain.user.exception.UserProfileImgFailException;
import backend.techeerzip.domain.user.exception.UserUnauthorizedAdminException;
import backend.techeerzip.domain.user.mapper.UserMapper;
import backend.techeerzip.domain.user.repository.PermissionRequestRepository;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.domain.userExperience.dto.request.CreateUserExperienceRequest;
import backend.techeerzip.domain.userExperience.entity.UserExperience;
import backend.techeerzip.domain.userExperience.exception.UserExperienceNotFoundException;
import backend.techeerzip.domain.userExperience.repository.UserExperienceRepository;
import backend.techeerzip.global.entity.StatusCategory;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserExperienceRepository userExperienceRepository;
    private final ResumeService resumeService;

    @Value("${PROFILE_IMG_URL}")
    private String profileImgUrl;

    @Value("${SLACK}")
    private String slackSecretKey;

    private final RestTemplate restTemplate;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final PermissionRequestRepository permissionRequestRepository;
    private final RoleRepository roleRepository;

    private final BlogRepository blogRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ResumeRepository resumeRepository;
    private final SessionRepository sessionRepository;
    private final StudyMemberRepository studyMemberRepository;

    private final UserMapper userMapper;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserService";

    private final TaskService taskService;

    @Transactional
    public void signUp(
            CreateUserWithResumeRequest createUserWithResumeRequest, MultipartFile file) {
        CreateUserRequest createUserRequest = createUserWithResumeRequest.getCreateUserRequest();
        CreateResumeRequest resumeRequest = createUserWithResumeRequest.getCreateResumeRequest();
        List<CreateUserExperienceRequest> experiences =
                createUserWithResumeRequest.getCreateUserExperienceRequest().getExperiences();

        if (!authService.checkTecheer(createUserRequest.getEmail())) {
            throw new AuthNotTecheerException();
        }

        if (!authService.checkEmailVerified(createUserRequest.getEmail())) {
            logger.warn("이메일 인증 필요 - email: {}", createUserRequest.getEmail(), CONTEXT);
            throw new AuthNotVerifiedEmailException();
        }

        if (file == null || file.isEmpty()) {
            logger.warn("이력서 파일 첨부 필요 - email: {}", createUserRequest.getEmail(), CONTEXT);
            throw new UserNotResumeException();
        }

        String profileImage = fetchProfileImg(createUserRequest.getEmail());

        Role defaultRole = roleRepository.findById(3L).orElseThrow(RoleNotFoundException::new);

        String password = createUserRequest.getPassword();
        String hashedPassword = passwordEncoder.encode(password);

        Optional<User> existingUserOpt = userRepository.findByEmail(createUserRequest.getEmail());

        User savedUser;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (!existingUser.isDeleted()) {
                throw new UserAlreadyExistsException();
            }

            existingUser.setDeleted(false);
            existingUser.setPassword(hashedPassword);
            existingUser.setName(createUserRequest.getName());
            existingUser.setMainPosition(createUserRequest.getMainPosition());
            existingUser.setSubPosition(createUserRequest.getSubPosition());
            existingUser.setGrade(createUserRequest.getGrade());
            existingUser.setGithubUrl(createUserRequest.getGithubUrl());
            existingUser.setTistoryUrl(createUserRequest.getTistoryUrl());
            existingUser.setVelogUrl(createUserRequest.getVelogUrl());
            existingUser.setMediumUrl(createUserRequest.getMediumUrl());
            existingUser.setSchool(createUserRequest.getSchool());
            existingUser.setLft(
                    createUserRequest.getIsLft() != null ? createUserRequest.getIsLft() : false);
            existingUser.setYear(
                    createUserRequest.getYear() != null ? createUserRequest.getYear() : -1);
            existingUser.setProfileImage(profileImage);
            existingUser.setAuth(true);
            existingUser.setRole(defaultRole);
            existingUser.setBootcampYear(null);

            savedUser = userRepository.save(existingUser);
            logger.info("재가입 회원 정보 등록 완료 - email: {}", createUserRequest.getEmail(), CONTEXT);

        } else {
            User newUser =
                    User.builder()
                            .email(createUserRequest.getEmail())
                            .password(hashedPassword)
                            .name(createUserRequest.getName())
                            .mainPosition(createUserRequest.getMainPosition())
                            .subPosition(createUserRequest.getSubPosition())
                            .grade(createUserRequest.getGrade())
                            .githubUrl(createUserRequest.getGithubUrl())
                            .tistoryUrl(createUserRequest.getTistoryUrl())
                            .velogUrl(createUserRequest.getVelogUrl())
                            .mediumUrl(createUserRequest.getMediumUrl())
                            .school(createUserRequest.getSchool())
                            .year(createUserRequest.getYear())
                            .isLft(createUserRequest.getIsLft())
                            .profileImage(profileImage)
                            .isAuth(true)
                            .bootcampYear(null)
                            .role(defaultRole)
                            .build();

            savedUser = userRepository.save(newUser);
            logger.info("신규 회원 정보 등록 완료 - email: {}", createUserRequest.getEmail(), CONTEXT);
        }

        // 블로그 크롤링 실행
        List<String> blogUrls =
                Stream.of(
                                savedUser.getTistoryUrl(),
                                savedUser.getVelogUrl(),
                                savedUser.getMediumUrl())
                        .filter(Objects::nonNull)
                        .filter(url -> !url.trim().isEmpty())
                        .toList();

        if (!blogUrls.isEmpty()) {
            taskService.requestSignUpBlogFetchForUser(savedUser.getId(), blogUrls);
        }

        // 이력서 저장
        resumeService.createResume(
                savedUser.getId(),
                file,
                resumeRequest.getTitle(),
                resumeRequest.getPosition(),
                resumeRequest.getCategory(),
                true);
        logger.info("이력서 저장 완료 - email: {}", createUserRequest.getEmail(), CONTEXT);

        List<UserExperience> experiencesData =
                experiences.stream()
                        .map(
                                exp ->
                                        UserExperience.builder()
                                                .userId(savedUser.getId())
                                                .position(exp.getPosition())
                                                .companyName(exp.getCompanyName())
                                                .startDate(exp.getStartDate().atStartOfDay())
                                                .endDate(
                                                        exp.getEndDate() != null
                                                                ? exp.getEndDate().atStartOfDay()
                                                                : null)
                                                .category(exp.getCategory())
                                                .isFinished(exp.getEndDate() != null)
                                                .description(exp.getDescription())
                                                .build())
                        .toList();

        userExperienceRepository.saveAll(experiencesData);
        logger.info("경력 등록 완료 - email: {}", createUserRequest.getEmail(), CONTEXT);
    }

    public void signUpExternal(CreateExternalUserRequest createExternalUserRequest) {

        String email = createExternalUserRequest.getEmail();

        if (!authService.checkEmailVerified(email)) {
            logger.warn("이메일 인증 필요 - email: {}", createExternalUserRequest.getEmail(), CONTEXT);
            throw new AuthNotVerifiedEmailException();
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        Long roleId =
                switch (createExternalUserRequest.getJoinReason()) {
                    case COMPANY -> 4L;
                    case BOOTCAMP -> 5L;
                };

        Role role = roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);

        String name = createExternalUserRequest.getName();
        String password = createExternalUserRequest.getPassword();
        String hashedPassword = passwordEncoder.encode(password);

        Integer bootcampYear = null;
        if (createExternalUserRequest.getJoinReason() == JoinReason.BOOTCAMP) {
            bootcampYear = BootcampPeriod.calculateGeneration(LocalDate.now());

            if (bootcampYear == null) {
                logger.warn("부트캠프 회원가입 실패 - email: {}", email, CONTEXT);
                throw new UserNotBootcampPeriodException();
            }
        }

        User user =
                User.builder()
                        .email(email)
                        .password(hashedPassword)
                        .name(name)
                        .role(role)
                        .isAuth(true)
                        .bootcampYear(bootcampYear)
                        .build();

        userRepository.save(user);
        logger.info("외부 회원가입 완료 - email: {}, role: {}", email, role.getName(), CONTEXT);
    }

    @Transactional
    public void deleteUser(Long userId, HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.delete();
        userRepository.save(user);
        logger.info("유저 데이터 삭제 완료 - userId: {}", userId, CONTEXT);

        blogRepository.updateIsDeletedByUserId(userId);
        bookmarkRepository.updateIsDeletedByUserId(userId);
        eventRepository.updateIsDeletedByUserId(userId);
        likeRepository.updateIsDeletedByUserId(userId);
        projectMemberRepository.updateIsDeletedByUserId(userId);
        resumeRepository.updateIsDeletedByUserId(userId);
        sessionRepository.updateIsDeletedByUserId(userId);
        studyMemberRepository.updateIsDeletedByUserId(userId);
        userExperienceRepository.updateIsDeletedByUserId(userId);

        logger.info("유저 연관 데이터 삭제 완료 - userId: {}", userId, CONTEXT);

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

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        authService.verifyCode(email, code);

        User user =
                userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional(readOnly = true)
    public GetUserResponse getUserInfo(Long userId) {
        User user =
                userRepository
                        .findByIdWithNonDeletedRelations(userId)
                        .orElseThrow(UserNotFoundException::new);
        return userMapper.toGetUserResponse(user);
    }

    @Transactional
    public GetProfileImgResponse updateProfileImg(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        String latestProfileImg = fetchProfileImg(user.getEmail());
        user.setProfileImage(latestProfileImg);
        userRepository.save(user);

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
                logger.info("최신 슬랙 이미지 불러오기 완료 - email: {}", email, CONTEXT);
                return (String) responseBody.get("image");
            }

            logger.warn("슬랙 이미지 요청 실패 - email: {}", email, CONTEXT);
            throw new UserProfileImgFailException();
        } catch (Exception e) {
            logger.warn("슬랙 이미지 요청 실패 - email: {}", email, CONTEXT);
            throw new UserProfileImgFailException();
        }
    }

    public PermissionRequest createUserPermissionRequest(Long userId, Long roleId) {
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

        permissionRequestRepository.updateStatusByUserId(userId, StatusCategory.APPROVED);
    }

    @Transactional
    public void updateProfile(
            Long userId, UpdateUserWithExperienceRequest updateUserWithExperienceRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        UpdateUserInfoRequest updateUserInfoRequest =
                updateUserWithExperienceRequest.getUpdateUserInfoRequest();

        if (updateUserInfoRequest != null) {
            if (updateUserInfoRequest.getMainPosition() != null)
                user.setMainPosition(updateUserInfoRequest.getMainPosition());
            if (updateUserInfoRequest.getSubPosition() != null)
                user.setSubPosition(updateUserInfoRequest.getSubPosition());
            if (updateUserInfoRequest.getGithubUrl() != null)
                user.setGithubUrl(updateUserInfoRequest.getGithubUrl());
            if (updateUserInfoRequest.getTistoryUrl() != null)
                user.setTistoryUrl(updateUserInfoRequest.getTistoryUrl());
            if (updateUserInfoRequest.getIsLft() != null)
                user.setLft(updateUserInfoRequest.getIsLft());
            if (updateUserInfoRequest.getSchool() != null)
                user.setSchool(updateUserInfoRequest.getSchool());
            if (updateUserInfoRequest.getGrade() != null)
                user.setGrade(updateUserInfoRequest.getGrade());
            if (updateUserInfoRequest.getMediumUrl() != null)
                user.setMediumUrl(updateUserInfoRequest.getMediumUrl());
            if (updateUserInfoRequest.getVelogUrl() != null)
                user.setVelogUrl(updateUserInfoRequest.getVelogUrl());
            if (updateUserInfoRequest.getYear() != null)
                user.setYear(updateUserInfoRequest.getYear());
        }

        // 경력 ID 있으면 경력 수정, 없으면 추가
        if (updateUserWithExperienceRequest.getUpdateUserExperienceRequest() != null
                && updateUserWithExperienceRequest.getUpdateUserExperienceRequest().getExperiences()
                        != null) {

            updateUserWithExperienceRequest
                    .getUpdateUserExperienceRequest()
                    .getExperiences()
                    .forEach(
                            e -> {
                                if (e.getExperienceId() != null) {
                                    UserExperience existingExp =
                                            userExperienceRepository
                                                    .findById(e.getExperienceId())
                                                    .orElseThrow(
                                                            UserExperienceNotFoundException::new);

                                    existingExp.setPosition(e.getPosition());
                                    existingExp.setCompanyName(e.getCompanyName());
                                    existingExp.setStartDate(e.getStartDate().atStartOfDay());
                                    existingExp.setEndDate(
                                            e.getEndDate() != null
                                                    ? e.getEndDate().atStartOfDay()
                                                    : null);
                                    existingExp.setCategory(e.getCategory());
                                    existingExp.setIsFinished(
                                            Boolean.TRUE.equals(e.getIsFinished()));
                                    existingExp.setDescription(e.getDescription());

                                    userExperienceRepository.save(existingExp);
                                } else {
                                    UserExperience newExp =
                                            UserExperience.builder()
                                                    .userId(user.getId())
                                                    .position(e.getPosition())
                                                    .companyName(e.getCompanyName())
                                                    .startDate(e.getStartDate().atStartOfDay())
                                                    .endDate(
                                                            e.getEndDate() != null
                                                                    ? e.getEndDate().atStartOfDay()
                                                                    : null)
                                                    .category(e.getCategory())
                                                    .isFinished(
                                                            Boolean.TRUE.equals(e.getIsFinished()))
                                                    .description(e.getDescription())
                                                    .build();

                                    userExperienceRepository.save(newExp);
                                }
                            });
        }
    }

    @Transactional
    public void deleteExperience(Long experienceId) {
        UserExperience experience =
                userExperienceRepository
                        .findById(experienceId)
                        .orElseThrow(UserExperienceNotFoundException::new);
        userExperienceRepository.delete(experience);
    }

    @Transactional
    public void updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Long roleId = user.getRole().getId();
        if (roleId == 3) {
            logger.warn("권한 없음 - userId: {}", userId);
            throw new UserUnauthorizedAdminException();
        }

        user.setNickname(nickname);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public GetUserProfileListResponse getAllProfiles(
            GetUserProfileListRequest getUserProfileListRequest) {
        int limit =
                getUserProfileListRequest.getLimit() != null
                        ? getUserProfileListRequest.getLimit()
                        : 10;
        String sortBy =
                getUserProfileListRequest.getSortBy() != null
                        ? getUserProfileListRequest.getSortBy()
                        : "year";

        List<User> users =
                userRepository
                        .findUsersWithCursor(
                                getUserProfileListRequest.getCursorId(),
                                getUserProfileListRequest.getPosition(),
                                getUserProfileListRequest.getYear(),
                                getUserProfileListRequest.getUniversity(),
                                getUserProfileListRequest.getGrade(),
                                limit + 1,
                                sortBy)
                        .stream()
                        .filter(
                                user -> {
                                    Long roleId =
                                            user.getRole() != null ? user.getRole().getId() : null;
                                    return roleId != null && roleId >= 0 && roleId <= 3;
                                })
                        .collect(Collectors.toList());

        boolean hasNext = users.size() > limit;
        if (hasNext) {
            users = users.subList(0, limit);
        }

        Long nextCursor = hasNext ? users.get(users.size() - 1).getId() : null;

        List<GetUserResponse> responses =
                users.stream().map(userMapper::toGetUserResponse).toList();

        return new GetUserProfileListResponse(responses, hasNext, nextCursor);
    }

    public <T> Map<Long, User> getIdAndUserMap(List<T> usersInfo, Function<T, Long> idExtractor) {
        final List<Long> usersId = usersInfo.stream().map(idExtractor).toList();
        final List<User> users = userRepository.findAllById(usersId);
        final Map<Long, User> userMap =
                users.stream().collect(Collectors.toMap(User::getId, user -> user));
        for (Long id : usersId) {
            if (!userMap.containsKey(id)) {
                throw new UserNotFoundException();
            }
        }
        return userMap;
    }
}
