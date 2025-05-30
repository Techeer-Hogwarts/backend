package backend.techeerzip.domain.event.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import backend.techeerzip.domain.event.dto.request.EventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.EventCreateResponse;
import backend.techeerzip.domain.event.dto.response.EventGetResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

/**
 * 이 인터페이스는 Swagger 문서 생성을 위한 어노테이션 분리용입니다. 실제 컨트롤러에서는 구현하지 않으며, 메서드 호출 목적도 없습니다. 따라서 IntelliJ 등에서
 * 사용되지 않는 메서드로 표시될 수 있습니다.
 */
public interface EventSwagger {

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(schema = @Schema(implementation = EventCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventCreateResponse> createEvent(
            @Parameter(description = "카테고리") @RequestParam String category,
            @Parameter(description = "행사 이름") @RequestParam String title,
            @Parameter(description = "시작 날짜") @RequestParam String startDate,
            @Parameter(description = "종료 날짜") @RequestParam String endDate,
            @Parameter(description = "링크") @RequestParam(required = false) String url
    ) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 목록 조회 및 검색", description = "이벤트 목록을 조회하고 검색합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = EventGetResponse.class)))
    default ResponseEntity<List<EventGetResponse>> getEventList(
            @Parameter(description = "검색 조건") EventListQueryRequest query
    ) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "단일 이벤트 조회", description = "지정된 ID의 이벤트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = EventGetResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventGetResponse> getEvent(
            @Parameter(description = "이벤트 ID") @PathVariable Long eventId
    ) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 수정", description = "지정된 ID의 이벤트를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = EventCreateResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<EventCreateResponse> updateEvent(
            @Parameter(description = "이벤트 ID") @PathVariable Long eventId,
            @Parameter(description = "카테고리") @RequestParam String category,
            @Parameter(description = "행사 이름") @RequestParam String title,
            @Parameter(description = "시작 날짜") @RequestParam String startDate,
            @Parameter(description = "종료 날짜") @RequestParam String endDate,
            @Parameter(description = "링크") @RequestParam(required = false) String url
    ) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "이벤트 삭제", description = "지정된 ID의 이벤트를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> deleteEvent(
            @Parameter(description = "이벤트 ID") @PathVariable Long eventId
    ) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
