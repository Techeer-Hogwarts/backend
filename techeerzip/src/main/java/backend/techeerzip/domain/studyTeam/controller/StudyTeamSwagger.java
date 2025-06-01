package backend.techeerzip.domain.studyTeam.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.studyTeam.dto.request.StudyAddMembersRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyApplicantRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "StudyTeam", description = "스터디 팀 관련 API")
public interface StudyTeamSwagger {
    @Operation(summary = "스터디 상세 조회", description = "스터디 팀의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{studyTeamId}")
    ResponseEntity<StudyTeamDetailResponse> getDetail(
            @Parameter(description = "스터디 팀 ID") @PathVariable Long studyTeamId);

    @Operation(summary = "스터디 수정", description = "스터디 팀 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping(value = "/{studyTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Long> updateStudyTeam(
            @Parameter(description = "스터디 팀 ID") @PathVariable Long studyTeamId,
            @Parameter(
                            description = "스터디 결과 이미지 목록",
                            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                    @RequestPart(value = "resultImages", required = false)
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "스터디 수정 요청 DTO",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                    @RequestPart("updateStudyTeamRequest")
                    StudyTeamUpdateRequest request,
            @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 생성", description = "새로운 스터디 팀을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Long> createStudyTeam(
            @Parameter(
                            description = "스터디 결과 이미지 목록",
                            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                    @RequestPart(value = "resultImages", required = false)
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "스터디 생성 요청 DTO",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                    @RequestPart("createStudyTeamRequest")
                    StudyTeamCreateRequest request);

    @Operation(summary = "스터디 지원", description = "스터디 팀에 지원합니다.")
    @ApiResponse(responseCode = "200", description = "지원 성공")
    @PostMapping("/apply")
    ResponseEntity<Void> applyToStudyTeam(
            @RequestBody StudyTeamApplyRequest request,
            @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 지원 취소", description = "자신의 지원을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "취소 성공")
    @PatchMapping("/{studyTeamId}/cancel")
    ResponseEntity<Void> cancelApplication(
            @PathVariable Long studyTeamId, @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 지원자 목록 조회", description = "해당 스터디의 지원자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{studyTeamId}/applicants")
    ResponseEntity<List<StudyApplicantResponse>> getApplicants(
            @PathVariable Long studyTeamId, @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 지원자 수락", description = "스터디 팀의 지원자를 수락합니다.")
    @ApiResponse(responseCode = "200", description = "수락 성공")
    @PatchMapping("/applicants/accept")
    ResponseEntity<Void> acceptApplicant(
            @RequestBody StudyApplicantRequest request,
            @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 지원자 거절", description = "스터디 팀의 지원자를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @PatchMapping("/applicants/reject")
    ResponseEntity<Void> rejectApplicant(
            @RequestBody StudyApplicantRequest request,
            @Parameter(hidden = true) @UserId Long userId);

    @Operation(summary = "스터디 멤버 추가", description = "스터디 팀에 멤버를 추가합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 추가 성공")
    @PostMapping("/members")
    ResponseEntity<Void> addStudyMembers(
            @RequestBody StudyAddMembersRequest request,
            @Parameter(hidden = true) @UserId Long userId);
}
