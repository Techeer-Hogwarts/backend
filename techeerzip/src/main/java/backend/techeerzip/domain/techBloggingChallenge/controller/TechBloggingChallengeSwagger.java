package backend.techeerzip.domain.techBloggingChallenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.DeleteAllRoundsRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tech Blogging Challenge", description = "테크 블로깅 챌린지 API")
public interface TechBloggingChallengeSwagger {

    @Operation(summary = "여러 회차 일괄 생성(관리자용)", description = "특정 연도의 상/하반기 회차를 일괄 생성합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "회차 생성 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청 (과거 날짜 지정 등)"),
                @ApiResponse(responseCode = "409", description = "이미 존재하는 회차")
            })
    ResponseEntity<Void> createRounds(
            @Parameter(
                            description = "회차 일괄 생성 요청",
                            schema =
                                    @Schema(
                                            implementation = CreateRoundRequest.class,
                                            example =
                                                    """
                                        {
                                            "year": 2025,
                                            "isFirstHalf": true,
                                            "intervalWeeks": 2
                                        }
                                        """,
                                            description =
                                                    """
                                        year: 연도 (예: 2025, 현재 연도 이후만 가능)
                                        isFirstHalf: 상반기 여부 (true: 상반기(3~7월), false: 하반기(9~2월))
                                        intervalWeeks: 회차 간격 주 단위 (예: 2)
                                        """))
                    @RequestBody
                    CreateRoundRequest request);

    @Operation(summary = "단일 회차 생성(관리자용)", description = "하나의 회차를 생성합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "회차 생성 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청 (과거 날짜 지정 등)"),
                @ApiResponse(responseCode = "409", description = "이미 존재하는 회차")
            })
    ResponseEntity<Void> createSingleRound(
            @Parameter(
                            description = "단일 회차 생성 요청",
                            schema =
                                    @Schema(
                                            implementation = CreateSingleRoundRequest.class,
                                            example =
                                                    """
                                        {
                                            "startDate": "2025-03-01",
                                            "endDate": "2025-03-14",
                                            "sequence": 1,
                                            "isFirstHalf": true
                                        }
                                        """,
                                            description =
                                                    """
                                        startDate: 시작일 (예: 2025-03-01, 현재 날짜 이후만 가능)
                                        endDate: 종료일 (예: 2025-03-14)
                                        sequence: 회차 순번 (예: 1)
                                        isFirstHalf: 상반기 여부 (true: 상반기(3~7월), false: 하반기(9~2월))
                                        """))
                    @RequestBody
                    CreateSingleRoundRequest request);

    @Operation(summary = "회차 수정(관리자용)", description = "특정 회차의 시작일과 종료일을 수정합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "회차 수정 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                @ApiResponse(responseCode = "404", description = "존재하지 않는 회차")
            })
    ResponseEntity<Void> updateRound(
            @Parameter(
                            description = "회차 수정 요청",
                            schema =
                                    @Schema(
                                            implementation = UpdateRoundRequest.class,
                                            example =
                                                    """
                                        {
                                            "roundId": 1,
                                            "startDate": "2025-03-01",
                                            "endDate": "2025-03-14"
                                        }
                                        """,
                                            description =
                                                    """
                                        roundId: 수정할 회차 ID
                                        startDate: 수정할 시작일 (예: 2025-03-01)
                                        endDate: 수정할 종료일 (예: 2025-03-14)
                                        """))
                    @RequestBody
                    UpdateRoundRequest request);

    @Operation(summary = "단일 회차 삭제(관리자용)", description = "특정 회차를 삭제합니다. (soft delete)")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "회차 삭제 성공"),
                @ApiResponse(responseCode = "404", description = "존재하지 않는 회차")
            })
    ResponseEntity<Void> deleteRound(
            @Parameter(description = "삭제할 회차 ID", example = "1") @PathVariable Long roundId);

    @Operation(summary = "전체 회차 삭제(관리자용)", description = "특정 연도의 상/하반기 회차를 모두 삭제합니다. (soft delete)")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "회차 삭제 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    ResponseEntity<Void> deleteAllRounds(
            @Parameter(
                            description = "전체 회차 삭제 요청",
                            schema =
                                    @Schema(
                                            implementation = DeleteAllRoundsRequest.class,
                                            example =
                                                    """
                                        {
                                            "year": 2025,
                                            "isFirstHalf": true
                                        }
                                        """,
                                            description =
                                                    """
                                        year: 삭제할 연도 (예: 2025
                                        isFirstHalf: 상반기 여부 (true: 상반기(3~7월), false: 하반기(9~2월))
                                        """))
                    @RequestBody
                    DeleteAllRoundsRequest request);

    @Operation(summary = "회차 목록 조회(관리자용)", description = "모든 회차의 목록을 조회합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "조회 성공"),
                @ApiResponse(responseCode = "204", description = "조회된 회차 없음")
            })
    ResponseEntity<RoundListResponse> getRoundList();
}
