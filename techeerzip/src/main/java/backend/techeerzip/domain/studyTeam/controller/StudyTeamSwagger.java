package backend.techeerzip.domain.studyTeam.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.studyTeam.dto.request.StudyAddMembersRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyApplicantRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.global.exception.ErrorResponse;
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

    @Operation(summary = "스터디 생성", description = "새로운 스터디 팀을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Long> createStudyTeam(
            @Parameter(
                            description = "스터디 결과 이미지 목록",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart("resultImages")
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "스터디 생성 요청 DTO",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    StudyTeamCreateRequest.class)))
                    @RequestPart("createStudyTeamRequest")
                    StudyTeamCreateRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "스터디 수정", description = "스터디 팀 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    default ResponseEntity<Long> updateStudyTeam(
            @Parameter(description = "스터디 팀 ID") @PathVariable Long studyTeamId,
            @Parameter(
                            description = "스터디 결과 이미지 목록",
                            content =
                                    @Content(
                                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            schema = @Schema(type = "string", format = "binary")))
                    @RequestPart("resultImages")
                    List<MultipartFile> resultImages,
            @Parameter(
                            description = "스터디 수정 요청 DTO",
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    StudyTeamUpdateRequest.class)))
                    @RequestPart("updateStudyTeamRequest")
                    StudyTeamUpdateRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "스터디 모집 마감", description = "스터디 팀 모집 상태를 마감합니다.")
    @ApiResponse(responseCode = "200", description = "모집 마감 완료")
    default void closeRecruit() {}

    @Operation(summary = "스터디 삭제", description = "스터디 팀을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 완료")
    default void deleteStudyTeam() {}

    @Operation(summary = "스터디 팀 지원", description = "스터디 팀에 지원합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = StudyTeamApplyRequest.class)))
    @ApiResponse(responseCode = "200", description = "지원 성공")
    default void applyToStudyTeam() {}

    @Operation(summary = "스터디 팀 지원 취소", description = "자신의 스터디 지원을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "지원 취소 완료")
    default void cancelApplication() {}

    @Operation(summary = "스터디 팀 지원자 목록 조회", description = "스터디 팀 지원자 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = StudyApplicantResponse.class)))
    default void getApplicants() {}

    @Operation(summary = "스터디 팀 지원자 수락", description = "스터디 팀 지원자를 수락합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = StudyApplicantRequest.class)))
    @ApiResponse(responseCode = "200", description = "수락 성공")
    default void acceptApplicant() {}

    @Operation(summary = "스터디 팀 지원자 거절", description = "스터디 팀 지원자를 거절합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = StudyApplicantRequest.class)))
    @ApiResponse(responseCode = "200", description = "거절 성공")
    default void rejectApplicant() {}

    @Operation(summary = "스터디 팀 멤버 추가", description = "스터디 팀에 멤버를 추가합니다.")
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = StudyAddMembersRequest.class)))
    @ApiResponse(responseCode = "200", description = "멤버 추가 성공")
    default void addStudyMembers() {}
}
