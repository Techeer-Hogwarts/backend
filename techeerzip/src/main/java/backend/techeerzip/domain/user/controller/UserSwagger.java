package backend.techeerzip.domain.user.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.user.dto.request.CreateExternalUserRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "user", description = "유저 API")
public interface UserSwagger {
    @Operation(summary = "회원가입", description = "새로운 회원을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 회원가입 요청",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> signup(MultipartFile file, CreateUserWithResumeRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "외부인 회원가입", description = "새로운 외부인 회원을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 회원가입 요청",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> signupExternal(CreateExternalUserRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    default ResponseEntity<Void> deleteUser(Long userId, HttpServletResponse response) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 비밀번호를 재설정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "이메일 인증 실패",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> resetPassword(ResetUserPasswordRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "유저 조회", description = "토큰으로 유저 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "유저 조회 성공",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = GetUserResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "사용자 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<GetUserResponse> getUser(Long userId) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "프로필 사진 동기화", description = "슬랙 프로필 이미지를 동기화합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "슬랙 프로필 동기화 성공",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = GetProfileImgResponse.class)))
    })
    default ResponseEntity<GetProfileImgResponse> updateProfileImage(
            UpdateUserProfileImgRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "권한 요청", description = "유저가 권한 요청을 보냅니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공"),
    })
    default ResponseEntity<Void> requestPermission(Long userId, CreateUserPermissionRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "권한 요청 목록 조회", description = "관리자가 권한 요청 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "권한 요청 목록 조회 성공"),
        @ApiResponse(
                responseCode = "403",
                description = "관리자 권한 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<List<GetPermissionResponse>> getPermissionRequests(Long userId) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "권한 승인", description = "관리자가 권한 요청을 승인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "권한 요청 승인 성공"),
        @ApiResponse(
                responseCode = "403",
                description = "관리자 권한 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> approvePermission(Long userId, UpdateUserPermissionRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "특정 프로필 조회", description = "userId로 특정 유저 프로필을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "특정 유저 프로필 조회 성공",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = GetUserResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "사용자 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<GetUserResponse> getProfile(Long userId) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필 정보를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공"),
        @ApiResponse(
                responseCode = "404",
                description = "사용자 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> updateProfile(Long userId, UpdateUserWithExperienceRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "경력 삭제", description = "경력 정보를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "경력 삭제 성공"),
        @ApiResponse(
                responseCode = "404",
                description = "경력 정보 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> deleteExperience(Long experienceId) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(summary = "닉네임 업데이트", description = "멘토 이상의 권한을 가진 사람만 닉네임을 수정할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "닉네임 업데이트 성공"),
        @ApiResponse(
                responseCode = "403",
                description = "멘토 이상 권한 없음",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> updateNickname(Long userId, UpdateUserNicknameRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
            summary = "모든 프로필 조회",
            description = "조건에 맞는 모든 유저 프로필을 조회합니다. sortBy: 기본 정렬 값 -> year(기수+이름 순), name(이름 순)")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "모든 프로필 조회 성공",
                content =
                        @io.swagger.v3.oas.annotations.media.Content(
                                schema =
                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                implementation = GetUserProfileListResponse.class)))
    })
    default ResponseEntity<GetUserProfileListResponse> getAllProfiles(
            GetUserProfileListRequest req) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }
}
