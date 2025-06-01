package backend.techeerzip.domain.projectTeam.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 이 인터페이스는 Swagger 문서 생성을 위한 어노테이션 분리용입니다. 실제 컨트롤러에서는 구현하지 않으며, 메서드 호출 목적도 없습니다. 따라서 IntelliJ 등에서
 * 사용되지 않는 메서드로 표시될 수 있습니다.
 */
@Tag(name = "ProjectTeam", description = "프로젝트 팀 관련 API")
public interface ProjectTeamSwagger {

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트 팀을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
                responseCode = "409",
                description = "중복된 팀 이름",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Long> createProjectTeam(
            @Parameter(
                            description = "메인 이미지 파일",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart(value = "mainImage", required = false)
                    MultipartFile mainImage,
            @Parameter(
                            description = "결과 이미지 파일 목록",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart(value = "resultImages", required = false)
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "프로젝트 생성 요청 DTO",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    ProjectTeamCreateRequest
                                                                            .class)))
                    @RequestPart("createProjectTeamRequest")
                    ProjectTeamCreateRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "프로젝트 수정", description = "기존 프로젝트 팀의 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Long> updateProjectTeam(
            @Parameter(description = "프로젝트 팀 ID") @PathVariable Long projectTeamId,
            @Parameter(
                            description = "메인 이미지 파일",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart(value = "mainImage", required = false)
                    MultipartFile mainImage,
            @Parameter(
                            description = "결과 이미지 파일 목록",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart(value = "resultImages", required = false)
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "프로젝트 수정 요청 DTO",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    ProjectTeamUpdateRequest
                                                                            .class)))
                    @RequestPart("updateProjectTeamRequest")
                    ProjectTeamUpdateRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "모든 프로젝트/스터디 팀 조회", description = "조건에 따라 전체 팀 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SliceTeamsResponse.class)))
    default void getAllTeams() {}

    @Operation(summary = "프로젝트 모집 마감", description = "프로젝트 팀의 모집 상태를 마감합니다.")
    @ApiResponse(responseCode = "200", description = "모집 마감 완료")
    default void closeRecruit() {}

    @Operation(summary = "프로젝트 삭제", description = "프로젝트 팀을 소프트 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 완료")
    default void deleteProjectTeam() {}

    @Operation(summary = "프로젝트 지원", description = "프로젝트 팀에 지원합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = ProjectTeamApplyRequest.class)))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지원 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "모집이 종료되었거나 포지션이 닫혀있는 경우",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default void applyToProject() {}

    @Operation(summary = "프로젝트 지원자 목록 조회", description = "지원자 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content =
                    @Content(
                            schema =
                                    @Schema(implementation = ProjectMemberApplicantResponse.class)))
    default void getApplicants() {}

    @Operation(summary = "프로젝트 지원 취소", description = "해당 프로젝트에 대한 자신의 지원을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "지원 취소 완료")
    default void cancelApplication() {}

    @Operation(summary = "프로젝트 지원 수락", description = "지원자의 프로젝트 참여를 승인합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = ProjectApplicantRequest.class)))
    @ApiResponse(responseCode = "200", description = "수락 완료")
    default void acceptApplicant() {}

    @Operation(summary = "프로젝트 지원 거절", description = "지원자의 프로젝트 참여를 거절합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = ProjectApplicantRequest.class)))
    @ApiResponse(responseCode = "200", description = "거절 완료")
    default void rejectApplicant() {}
}
