package backend.techeerzip.domain.user.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.user.dto.request.CreateUserPermissionRequest;
import backend.techeerzip.domain.user.dto.request.CreateUserWithResumeRequest;
import backend.techeerzip.domain.user.dto.request.GetUserProfileListRequest;
import backend.techeerzip.domain.user.dto.request.ResetUserPasswordRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserNicknameRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserPermissionRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserProfileImgRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserWithExperienceRequest;
import backend.techeerzip.domain.user.dto.response.GetPermissionResponse;
import backend.techeerzip.domain.user.dto.response.GetProfileImgResponse;
import backend.techeerzip.domain.user.dto.response.GetUserProfileListResponse;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.service.UserService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/users")
@RequiredArgsConstructor
public class UserController implements UserSwagger {
    private final UserService userService;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserController";

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<Void> signup(
            @RequestPart("file") MultipartFile file,
            @RequestPart("createUserWithResumeRequest") @Valid
                    CreateUserWithResumeRequest createUserWithResumeRequest) {
        logger.info(
                "회원가입 요청 처리 중 - email: {}",
                createUserWithResumeRequest.getCreateUserRequest().getEmail(),
                CONTEXT);
        userService.signUp(createUserWithResumeRequest, file);
        logger.info(
                "회원가입 요청 처리 완료 - email: {}",
                createUserWithResumeRequest.getCreateUserRequest().getEmail(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "")
    @Override
    public ResponseEntity<Void> deleteUser(
            @Valid @Parameter(hidden = true) @UserId Long userId, HttpServletResponse response) {
        logger.info("회원 탈퇴 요청 처리 중 - userId: {}", userId, CONTEXT);
        userService.deleteUser(userId, response);
        logger.info("회원 탈퇴 요청 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/findPwd")
    @Override
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetUserPasswordRequest userResetPasswordRequest) {
        String email = userResetPasswordRequest.getEmail();
        String code = userResetPasswordRequest.getCode();
        String newPassword = userResetPasswordRequest.getNewPassword();

        logger.info("비밀번호 재설정 요청 처리 중 - email: {}", userResetPasswordRequest.getEmail(), CONTEXT);
        userService.resetPassword(email, code, newPassword);
        logger.info("비밀번호 재설정 요청 처리 완료 - email: {}", userResetPasswordRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    @Override
    public ResponseEntity<GetUserResponse> getUser(
            @Valid @Parameter(hidden = true) @UserId Long userId) {
        logger.info("유저 정보 조회 요청 처리 중 - userId: {}", userId, CONTEXT);
        GetUserResponse response = userService.getUserInfo(userId);
        logger.info("유저 정보 조회 요청 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profileImage")
    @Override
    public ResponseEntity<GetProfileImgResponse> updateProfileImage(
            @Valid @RequestBody UpdateUserProfileImgRequest updateUserProfileImgRequest) {
        String email = updateUserProfileImgRequest.getEmail();

        logger.info("프로필 사진 동기화 요청 처리 중 - email: {}", email, CONTEXT);
        GetProfileImgResponse response = userService.updateProfileImg(email);
        logger.info("프로필 사진 동기화 요청 처리 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/permission/request")
    @Override
    public ResponseEntity<Void> requestPermission(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody CreateUserPermissionRequest createUserPermissionRequest) {
        logger.info(
                "권한 요청 처리 중 - userId: {}, newRoleId: {}",
                userId,
                createUserPermissionRequest.getRoleId(),
                CONTEXT);
        userService.createUserPermissionRequest(userId, createUserPermissionRequest.getRoleId());
        logger.info(
                "권한 요청 처리 완료 - userId: {}",
                userId,
                createUserPermissionRequest.getRoleId(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permission/request")
    @Override
    public ResponseEntity<List<GetPermissionResponse>> getPermissionRequests(
            @Valid @Parameter(hidden = true) @UserId Long userId) {
        logger.info("권한 요청 목록 조회 요청 처리 중", CONTEXT);
        List<GetPermissionResponse> response = userService.getAllPendingPermissionRequests(userId);
        logger.info("권한 요청 목록 조회 요청 처리 완료", CONTEXT);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/permission/approve")
    @Override
    public ResponseEntity<Void> approvePermission(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody UpdateUserPermissionRequest updateUserPermissionRequest) {
        logger.info(
                "권한 승인 요청 처리 중 - userId: {}, newRoleId: {}",
                updateUserPermissionRequest.getUserId(),
                updateUserPermissionRequest.getNewRoleId(),
                CONTEXT);
        userService.approveUserPermission(
                userId,
                updateUserPermissionRequest.getUserId(),
                updateUserPermissionRequest.getNewRoleId());
        logger.info(
                "권한 승인 요청 처리 완료 - userId: {}, newRoleId: {}",
                updateUserPermissionRequest.getUserId(),
                updateUserPermissionRequest.getNewRoleId(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<GetUserResponse> getProfile(@PathVariable Long userId) {
        logger.info("특정 프로필 조회 요청 처리 중 - profileUserId: {}", userId, CONTEXT);
        GetUserResponse response = userService.getUserInfo(userId);
        logger.info("특정 프로필 조회 요청 처리 완료 - profileUserId: {}", userId, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("")
    @Override
    public ResponseEntity<Void> updateProfile(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody UpdateUserWithExperienceRequest updateUserWithExperienceRequest) {
        logger.info("프로필 업데이트 요청 처리 중 - userId: {}", userId, CONTEXT);
        userService.updateProfile(userId, updateUserWithExperienceRequest);
        logger.info("프로필 업데이트 요청 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/experience/{experienceId}")
    @Override
    public ResponseEntity<Void> deleteExperience(@PathVariable Long experienceId) {
        logger.info("경력 삭제 요청 처리 중 - experienceId: {}", experienceId, CONTEXT);
        userService.deleteExperience(experienceId);
        logger.info("경력 삭제 요청 처리 완료 - experienceId: {}", experienceId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/nickname")
    @Override
    public ResponseEntity<Void> updateNickname(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody UpdateUserNicknameRequest updateUserNicknameRequest) {
        logger.info(
                "닉네임 업데이트 요청 처리 중 - userId: {}, newNickname: {}",
                userId,
                updateUserNicknameRequest.getNickname(),
                CONTEXT);
        userService.updateNickname(userId, updateUserNicknameRequest.getNickname());
        logger.info(
                "닉네임 업데이트 요청 처리 완료 - userId: {}, newNickname: {}",
                userId,
                updateUserNicknameRequest.getNickname(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles")
    @Override
    public ResponseEntity<GetUserProfileListResponse> getAllProfiles(
            @ModelAttribute GetUserProfileListRequest getUserProfileListRequest) {
        logger.info("모든 프로필 조회 요청 처리 중 - sort: {}", getUserProfileListRequest.getSortBy(), CONTEXT);
        GetUserProfileListResponse profiles = userService.getAllProfiles(getUserProfileListRequest);
        logger.info(
                "모든 프로필 조회 요청 처리 완료 - sort: {}", getUserProfileListRequest.getSortBy(), CONTEXT);
        return ResponseEntity.ok(profiles);
    }
}
