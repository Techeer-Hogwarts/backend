package backend.techeerzip.domain.event.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import backend.techeerzip.domain.event.dto.request.EventCreateRequest;
import backend.techeerzip.domain.event.dto.request.EventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.EventCreateResponse;
import backend.techeerzip.domain.event.dto.response.EventListResponse;
import backend.techeerzip.domain.event.dto.response.EventResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface EventSwagger {

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = @Content(schema = @Schema(implementation = EventCreateResponse.class))),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventCreateResponse> createEvent(
            @Valid
                    @RequestBody(
                            description = "이벤트 생성 요청 객체",
                            required = true,
                            content =
                                    @Content(
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    EventCreateRequest.class)))
                    EventCreateRequest request,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 목록 조회 및 검색", description = "이벤트 목록을 조회하고 검색합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content =
                        @Content(
                                array =
                                        @ArraySchema(
                                                schema =
                                                        @Schema(
                                                                implementation =
                                                                        EventResponse.class))))
    })
    default ResponseEntity<EventListResponse> getEventList(
            @Parameter(description = "검색 조건") EventListQueryRequest query) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "단일 이벤트 조회", description = "지정된 ID의 이벤트를 조회합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = EventResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "이벤트를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventResponse> getEvent(
            @Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 수정", description = "지정된 ID의 이벤트를 수정합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수정 성공",
                content = @Content(schema = @Schema(implementation = EventCreateResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "이벤트를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventCreateResponse> updateEvent(
            @Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId,
            @RequestBody(
                            description = "이벤트 수정 요청 객체",
                            required = true,
                            content =
                                    @Content(
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    EventCreateRequest.class)))
                    EventCreateRequest request,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 삭제", description = "지정된 ID의 이벤트를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(
                responseCode = "404",
                description = "이벤트를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> deleteEvent(
            @Parameter(description = "이벤트 ID", required = true) @PathVariable Long eventId,
            @Parameter(hidden = true) Long userId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
