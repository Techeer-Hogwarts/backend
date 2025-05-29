package backend.techeerzip.domain.user.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import backend.techeerzip.domain.user.dto.request.UpdateUserPermissionRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserProfileImgRequest;
import backend.techeerzip.domain.user.dto.request.UpdateUserWithExperienceRequest;
import backend.techeerzip.domain.user.dto.request.UserResetPasswordRequest;
import backend.techeerzip.domain.user.dto.response.GetPermissionResponse;
import backend.techeerzip.domain.user.dto.response.GetProfileImgResponse;
import backend.techeerzip.domain.user.dto.response.GetUserResponse;
import backend.techeerzip.domain.user.service.UserService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "user", description = "유저 API")
@RestController
@RequestMapping("/api/v3/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CustomLogger logger;
    private static final String CONTEXT = "UserController";

    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> signup(
            @RequestPart("file") MultipartFile file,
            @RequestPart("createUserWithResumeRequest") @Valid
                    CreateUserWithResumeRequest createUserWithResumeRequest) {
        userService.signUp(createUserWithResumeRequest, file);
        logger.info(
                "회원가입 처리 완료 - email: {}",
                createUserWithResumeRequest.getCreateUserRequest().getEmail(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 삭제합니다.")
    @DeleteMapping(value = "")
    public ResponseEntity<Void> deleteUser(
            @Valid @Parameter(hidden = true) @UserId Long userId, HttpServletResponse response) {
        userService.deleteUser(userId, response);
        logger.info("회원 탈퇴 처리 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 비밀번호를 재설정합니다.")
    @PatchMapping("/findPwd")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody UserResetPasswordRequest userResetPasswordRequest) {
        String email = userResetPasswordRequest.getEmail();
        String code = userResetPasswordRequest.getCode();
        String newPassword = userResetPasswordRequest.getNewPassword();

        userService.resetPassword(email, code, newPassword);
        logger.info("비밀번호 재설정 요청 처리 완료 - email: {}", userResetPasswordRequest.getEmail(), CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 조회", description = "토큰으로 유저 정보를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<GetUserResponse> getUser(
            @Valid @Parameter(hidden = true) @UserId Long userId) {
        GetUserResponse response = userService.getUserInfo(userId);
        logger.info("유저 정보 조회 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 사진 동기화", description = "슬랙 프로필 이미지를 동기화합니다.")
    @PatchMapping("/profileImage")
    public ResponseEntity<GetProfileImgResponse> updateProfileImage(
            @Valid @RequestBody UpdateUserProfileImgRequest updateUserProfileImgRequest) {
        String email = updateUserProfileImgRequest.getEmail();
        GetProfileImgResponse response = userService.updateProfileImg(email);
        logger.info("프로필 사진 동기화 완료 - email: {}", email, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "권한 요청", description = "유저가 권한 요청을 보냅니다.")
    @PostMapping("/permission/request")
    public ResponseEntity<Void> requestPermission(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody CreateUserPermissionRequest createUserPermissionRequest) {
        userService.createUserPermissionRequest(userId, createUserPermissionRequest.getRoleId());
        logger.info("권한 요청 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "권한 요청 목록 조회", description = "관리자가 권한 요청 목록을 조회합니다.")
    @GetMapping("/permission/request")
    public ResponseEntity<List<GetPermissionResponse>> getPermissionRequests(
            @Valid @Parameter(hidden = true) @UserId Long userId) {
        List<GetPermissionResponse> response = userService.getAllPendingPermissionRequests(userId);
        logger.info("권한 요청 목록 조회 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "권한 승인", description = "관리자가 권한 요청을 승인합니다.")
    @PatchMapping("/permission/approve")
    public ResponseEntity<Void> approvePermission(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody UpdateUserPermissionRequest updateUserPermissionRequest) {
        userService.approveUserPermission(
                userId,
                updateUserPermissionRequest.getUserId(),
                updateUserPermissionRequest.getNewRoleId());
        logger.info(
                "권한 승인 완료 - userId: {}, newRoleId: {}",
                updateUserPermissionRequest.getUserId(),
                updateUserPermissionRequest.getNewRoleId(),
                CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 프로필 조회", description = "userId로 특정 유저 프로필을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponse> getProfile(@PathVariable Long userId) {
        GetUserResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필 정보를 업데이트합니다.")
    @PatchMapping("")
    public ResponseEntity<Void> updateProfile(
            @Valid @Parameter(hidden = true) @UserId Long userId,
            @RequestBody UpdateUserWithExperienceRequest updateUserWithExperienceRequest) {
        userService.updateProfile(userId, updateUserWithExperienceRequest);
        logger.info("프로필 업데이트 완료 - userId: {}", userId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "경력 삭제", description = "경력 정보를 삭제합니다.")
    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long experienceId) {
        userService.deleteExperience(experienceId);
        logger.info("경력 삭제 완료 - experienceId: {}", experienceId, CONTEXT);
        return ResponseEntity.ok().build();
    }
}
