package backend.techeerzip.domain.session.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 이 인터페이스는 Swagger 문서 생성을 위한 어노테이션 분리용입니다. 실제 컨트롤러에서는 구현하지 않으며, 메서드 호출 목적도 없습니다. 따라서 IntelliJ 등에서
 * 사용되지 않는 메서드로 표시될 수 있습니다.
 */
@Tag(name = "sessions", description = "세션 API")
public interface SessionSwagger {

    @Operation(summary = "세션 게시물 게시", description = "세션 게시물을 게시합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "C001 - Invalid Input Value",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"C001\", \"message\": \"Invalid Input Value\", \"errors\": {\"title\": \"제목은 필수입니다.\"}}"))),
        @ApiResponse(
                responseCode = "401",
                description = "A002 - 유효하지 않은 JWT 토큰입니다.",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"A002\", \"message\": \"유효하지 않은 JWT 토큰입니다.\", \"errors\": null}")))
    })
    default ResponseEntity<Long> createSession(
            @Parameter(description = "세션 생성 요청") @RequestBody @Valid SessionCreateRequest request,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "세션 게시물 수정", description = "세션 게시물을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "C001 - Invalid Input Value",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"C001\", \"message\": \"Invalid Input Value\", \"errors\": {\"title\": \"제목은 필수입니다.\"}}"))),
        @ApiResponse(
                responseCode = "401",
                description = "A002 - 유효하지 않은 JWT 토큰입니다.",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"A002\", \"message\": \"유효하지 않은 JWT 토큰입니다.\", \"errors\": null}"))),
        @ApiResponse(
                responseCode = "403",
                description = "SS003 - 해당 세션에 대한 권한이 없습니다.",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"SS003\", \"message\": \"해당 세션에 대한 권한이 없습니다.\", \"errors\": null}"))),
        @ApiResponse(
                responseCode = "404",
                description = "SS001 - 해당 세션을 찾을 수 없습니다",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"SS001\", \"message\": \"해당 세션을 찾을 수 없습니다\", \"errors\": null}")))
    })
    default ResponseEntity<Void> updateSession(
            @Parameter(description = "세션 수정 요청") @RequestBody @Valid SessionCreateRequest request,
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "세션 게시물 목록 조회", description = "세션 게시물 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SessionListResponse.class)))
    default ResponseEntity<SessionListResponse<SessionResponse>> getAllSessions(
            @Parameter(description = "검색 조건") @Valid SessionListQueryRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "세션 단일 조회", description = "세션 ID를 기반으로 단일 세션을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = SessionResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "SS001 - 해당 세션을 찾을 수 없습니다",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"SS001\", \"message\": \"해당 세션을 찾을 수 없습니다\", \"errors\": null}")))
    })
    default ResponseEntity<SessionResponse> getSessionBySessionId(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "세션 게시물 삭제", description = "세션 게시물을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(
                responseCode = "401",
                description = "A002 - 유효하지 않은 JWT 토큰입니다.",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"A002\", \"message\": \"유효하지 않은 JWT 토큰입니다.\", \"errors\": null}"))),
        @ApiResponse(
                responseCode = "403",
                description = "SS003 - 해당 세션에 대한 권한이 없습니다.",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"SS003\", \"message\": \"해당 세션에 대한 권한이 없습니다.\", \"errors\": null}"))),
        @ApiResponse(
                responseCode = "404",
                description = "SS001 - 해당 세션을 찾을 수 없습니다",
                content =
                        @Content(
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"code\": \"SS001\", \"message\": \"해당 세션을 찾을 수 없습니다\", \"errors\": null}")))
    })
    default ResponseEntity<Void> deleteSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "세션 인기 게시물 목록 조회", description = "세션 인기 게시물 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = SessionBestListResponse.class)))
    default ResponseEntity<SessionBestListResponse<SessionResponse>> getAllBestSessions(
            @Parameter(description = "검색 조건") @Valid SessionBestListRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
