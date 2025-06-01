package backend.techeerzip.domain.projectTeam.controller;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 이 인터페이스는 Swagger 문서 생성을 위한 어노테이션 분리용입니다. 실제 컨트롤러에서는 구현하지 않으며, 메서드 호출 목적도 없습니다. 따라서 IntelliJ 등에서
 * 사용되지 않는 메서드로 표시될 수 있습니다.
 */
@Tag(name = "ProjectTeam", description = "프로젝트 팀 관련 API")
@RequestMapping("/api/v3/projectTeams")
public interface ProjectTeamSwagger {

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 팀의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProjectTeamDetailResponse.class)))
    @GetMapping("/{projectTeamId}")
    ResponseEntity<ProjectTeamDetailResponse> getDetail(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId);

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트 팀을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 팀 이름", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Long> createProjectTeam(
            @Parameter(description = "메인 이미지", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "mainImage") MultipartFile mainImage,

            @Parameter(description = "결과 이미지 리스트", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,

            @RequestBody(
                    description = "생성 요청 DTO",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProjectTeamCreateRequest.class))
            )
            @RequestPart("createProjectTeamRequest") ProjectTeamCreateRequest request
    );

    @Operation(summary = "프로젝트 수정", description = "기존 프로젝트 팀의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping(value = "/{projectTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Long> updateProjectTeam(
            @Parameter(description = "수정할 프로젝트 팀 ID") @PathVariable Long projectTeamId,

            @Parameter(description = "메인 이미지", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,

            @Parameter(description = "결과 이미지 리스트", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,

            @RequestBody(
                    description = "수정 요청 DTO",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProjectTeamUpdateRequest.class))
            )
            @RequestPart("updateProjectTeamRequest") ProjectTeamUpdateRequest request,

            @Parameter(hidden = true)
            @UserId Long userId
    );

    @Operation(summary = "모든 프로젝트/스터디 팀 조회", description =
            """
커서 기반으로 프로젝트와 스터디 팀 목록을 조회합니다.

[기본 조회] GET /api/v1/allTeams
- 전체 조회
- 최신순 정렬 UPDATE_AT_DESC
- limit 10개

[정렬 방식에 따라 사용하는 커서 필드]
- UPDATE_AT_DESC: dateCursor, id 필요
- VIEW_COUNT_DESC, LIKE_COUNT_DESC: countCursor, id 필요

[필드 null 처리 동작]
- sortType이 null인 경우 기본값은 UPDATE_AT_DESC
- id가 null이면 커서 보조키 없이 정렬 필드만으로 조회
- dateCursor, countCursor가 null이면 최신순 조회
- isRecruited, isFinished가 null이면 모든 팀 포함

[두 번째 조회 필수 필드]
Response
- id
- createAt
- sortType(UPDATE_AT_DESC 등)
- cursor data

""")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    implementation = GetAllTeamsResponse.class,
                    oneOf = {ProjectSliceTeamsResponse.class, StudySliceTeamsResponse.class}
            )
    ))
    @GetMapping("/allTeams")
    ResponseEntity<GetAllTeamsResponse> getAllTeams(
            @ModelAttribute @Valid GetTeamsQueryRequest request);

    @Operation(summary = "모집 마감", description = "프로젝트 팀의 모집을 마감합니다.")
    @ApiResponse(responseCode = "200", description = "모집 마감 성공")
    @PatchMapping("/close/{projectTeamId}")
    ResponseEntity<Void> closeRecruit(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "팀 삭제", description = "프로젝트 팀을 소프트 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @PatchMapping("/delete/{projectTeamId}")
    ResponseEntity<Void> deleteProjectTeam(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "지원자 목록 조회", description = "해당 팀에 지원한 지원자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProjectMemberApplicantResponse.class)))
    @GetMapping("/{projectTeamId}/applicants")
    ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "팀 지원", description = "해당 팀에 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지원 성공"),
            @ApiResponse(responseCode = "400", description = "모집이 마감되었거나 포지션이 닫혀 있음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/apply")
    ResponseEntity<Void> applyToProject(
            @RequestBody ProjectTeamApplyRequest request,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "지원 취소", description = "해당 팀에 대한 본인의 지원을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "취소 성공")
    @PatchMapping("/{projectTeamId}/cancel")
    ResponseEntity<Void> cancelApplication(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "지원 수락", description = "팀장이 지원자를 수락합니다.")
    @ApiResponse(responseCode = "200", description = "수락 성공")
    @PatchMapping("/accept")
    ResponseEntity<Void> acceptApplicant(
            @RequestBody ProjectApplicantRequest request,
            @Parameter(hidden = true) @UserId Long userId
    );

    @Operation(summary = "지원 거절", description = "팀장이 지원자를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @PatchMapping("/reject")
    ResponseEntity<Void> rejectApplicant(
            @RequestBody ProjectApplicantRequest request,
            @Parameter(hidden = true) @UserId Long userId
    );
}
