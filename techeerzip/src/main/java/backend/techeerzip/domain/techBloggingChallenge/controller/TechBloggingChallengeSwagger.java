package backend.techeerzip.domain.techBloggingChallenge.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.ApplyChallengeRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateTermRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.request.UpdateRoundRequest;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.AttendanceStatusResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.BlogChallengeListResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.RoundDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermDetailResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermRoundsSummaryResponse;
import backend.techeerzip.domain.techBloggingChallenge.dto.response.TermSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tech Blogging Challenge", description = "기술 블로깅 챌린지 API")
public interface TechBloggingChallengeSwagger {

    @Operation(
            summary = "챌린지 지원",
            description = "특정 분기(연도/반기)의 챌린지에 지원합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "챌린지 지원 성공"),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "이미 참여한 챌린지",
                                                    value =
                                                            """
                            {
                                "message": "이미 참여한 챌린지입니다."
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @PostMapping("/apply")
    ResponseEntity<Void> applyChallenge(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @RequestBody ApplyChallengeRequest request);

    @Operation(
            summary = "챌린지 기간 생성",
            description = "상반기/하반기 챌린지 기간과 회차를 생성합니다. 관리자만 사용할 수 있는 API입니다. 연동필요 X",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "챌린지 기간 생성 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = TermDetailResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "2024년 상반기 챌린지 생성",
                                                    value =
                                                            """
                            {
                                "id": 1,
                                "termName": "2024년 상반기",
                                "year": 2024,
                                "firstHalf": true,
                                "createdAt": "2024-03-01T10:00:00",
                                "rounds": [
                                    {
                                        "id": 1,
                                        "termId": 1,
                                        "roundName": "1회차",
                                        "termName": "2024년 상반기",
                                        "sequence": 1,
                                        "startDate": "2024-03-01",
                                        "endDate": "2024-03-14",
                                        "year": 2024,
                                        "firstHalf": true
                                    }
                                ]
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "이미 존재하는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "이미 존재하는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @PostMapping("/terms")
    ResponseEntity<TermDetailResponse> createTerm(@Valid @RequestBody CreateTermRequest request);

    @Operation(
            summary = "챌린지 기간 조회",
            description = "특정 챌린지 기간의 상세 정보를 조회합니다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "챌린지 기간 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = TermDetailResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "챌린지 기간 조회 결과",
                                                    value =
                                                            """
                            {
                                "id": 1,
                                "termName": "2024년 상반기",
                                "year": 2024,
                                "firstHalf": true,
                                "createdAt": "2024-03-01T10:00:00",
                                "rounds": [
                                    {
                                        "id": 1,
                                        "termId": 1,
                                        "roundName": "1회차",
                                        "termName": "2024년 상반기",
                                        "sequence": 1,
                                        "startDate": "2024-03-01",
                                        "endDate": "2024-03-14",
                                        "year": 2024,
                                        "firstHalf": true
                                    }
                                ]
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @GetMapping("/terms/{termId}")
    ResponseEntity<TermDetailResponse> getTerm(
            @Parameter(description = "챌린지 기간 ID", example = "1") @PathVariable Long termId);

    @Operation(
            summary = "챌린지 기간 삭제",
            description = "특정 챌린지 기간과 관련 회차를 삭제합니다. 관리자만 사용할 수 있는 API입니다. 연동필요 X",
            responses = {
                @ApiResponse(responseCode = "204", description = "챌린지 기간 삭제 성공"),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @DeleteMapping("/terms/{termId}")
    ResponseEntity<Void> deleteTerm(
            @Parameter(description = "챌린지 기간 ID", example = "1") @PathVariable Long termId);

    @Operation(
            summary = "단일 회차 생성",
            description = "특정 챌린지 기간에 단일 회차를 생성합니다. 관리자만 사용할 수 있는 API입니다. 연동필요 X",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "회차 생성 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(implementation = RoundDetailResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "회차 생성 결과",
                                                    value =
                                                            """
                            {
                                "id": 1,
                                "termId": 1,
                                "roundName": "1회차",
                                "termName": "2024년 상반기",
                                "sequence": 1,
                                "startDate": "2024-03-01",
                                "endDate": "2024-03-14",
                                "year": 2024,
                                "firstHalf": true
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "기간이 겹치는 회차",
                                                    value =
                                                            """
                            {
                                "message": "이미 존재하는 회차 기간입니다."
                            }
                            """),
                                            @ExampleObject(
                                                    name = "과거 날짜",
                                                    value =
                                                            """
                            {
                                "message": "과거 날짜는 사용할 수 없습니다."
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @PostMapping("/terms/rounds")
    ResponseEntity<RoundDetailResponse> createRound(
            @Valid @RequestBody CreateSingleRoundRequest request);

    @Operation(
            summary = "회차 수정",
            description = "특정 회차의 시작/종료 날짜를 수정합니다. 관리자만 사용할 수 있는 API입니다. 연동필요 X",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "회차 수정 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(implementation = RoundDetailResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "회차 수정 결과",
                                                    value =
                                                            """
                            {
                                "id": 1,
                                "termId": 1,
                                "roundName": "1회차",
                                "termName": "2024년 상반기",
                                "sequence": 1,
                                "startDate": "2024-03-01",
                                "endDate": "2024-03-21",
                                "year": 2024,
                                "firstHalf": true
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "기간이 너무 짧음",
                                                    value =
                                                            """
                            {
                                "message": "회차 기간은 최소 13일 이상이어야 합니다."
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "회차를 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 회차",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 회차입니다."
                            }
                            """)
                                        }))
            })
    @PutMapping("/rounds")
    ResponseEntity<RoundDetailResponse> updateRound(@Valid @RequestBody UpdateRoundRequest request);

    @Operation(
            summary = "회차 삭제",
            description = "특정 회차를 삭제합니다. 관리자만 사용할 수 있는 API입니다. 연동필요 X",
            responses = {
                @ApiResponse(responseCode = "204", description = "회차 삭제 성공"),
                @ApiResponse(
                        responseCode = "404",
                        description = "회차를 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 회차",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 회차입니다."
                            }
                            """)
                                        }))
            })
    @DeleteMapping("/rounds/{roundId}")
    ResponseEntity<Void> deleteRound(
            @Parameter(description = "회차 ID", example = "1") @PathVariable Long roundId);

    @Operation(
            summary = "회차별 블로그 조회",
            description =
                    """
            챌린지 회차별 블로그를 커서 기반으로 조회합니다.

            - termId가 없으면 현재 진행중인 챌린지의 블로그를 조회합니다.
            - roundId는 반드시 termId와 함께 사용해야 합니다.
            - cursorId는 이전 조회의 마지막 블로그 ID입니다.
            - sortBy 옵션: latest(최신순), viewCount(조회순), name(이름순)
            """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "블로그 목록 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                BlogChallengeListResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "최신순 조회 예시",
                                                    value =
                                                            """
                            {
                                "blogs": [
                                    {
                                        "id": 1,
                                        "title": "Spring Boot 시작하기",
                                        "url": "https://example.com/blog/1",
                                        "authorName": "홍길동",
                                        "createdAt": "2024-03-01T10:00:00",
                                        "viewCount": 100,
                                        "likeCount": 10
                                    }
                                ],
                                "hasNext": true
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "회차 ID만 있는 경우",
                                                    value =
                                                            """
                            {
                                "message": "회차 ID만으로는 조회할 수 없습니다. 챌린지 기간(termId)을 함께 입력해주세요."
                            }
                            """),
                                            @ExampleObject(
                                                    name = "잘못된 회차 ID",
                                                    value =
                                                            """
                            {
                                "message": "회차 ID 1은 챌린지 기간 ID 2에 속하지 않습니다."
                            }
                            """)
                                        }))
            })
    @GetMapping("/rounds/blogs")
    ResponseEntity<BlogChallengeListResponse> getBlogsByRoundCursor(
            @Parameter(description = "챌린지 기간 ID (없으면 현재 진행중인 챌린지)", example = "1")
                    @RequestParam(required = false)
                    Long termId,
            @Parameter(description = "회차 ID (termId와 함께 사용)", example = "1")
                    @RequestParam(required = false)
                    Long roundId,
            @Parameter(description = "커서 ID (이전 조회의 마지막 블로그 ID)", example = "100")
                    @RequestParam(required = false)
                    Long cursorId,
            @Parameter(description = "조회할 개수", example = "10")
                    @RequestParam(required = false, defaultValue = "10")
                    int limit,
            @Parameter(description = "정렬 기준 (latest/viewCount/name)", example = "latest")
                    @RequestParam(required = false, defaultValue = "latest")
                    String sortBy);

    @Operation(
            summary = "챌린지 기간 목록 조회",
            description = "모든 챌린지 기간의 요약 정보를 조회합니다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "챌린지 기간 목록 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(implementation = TermSummaryResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "챌린지 기간 목록",
                                                    value =
                                                            """
                            [
                                {
                                    "id": 1,
                                    "termName": "2024년 상반기",
                                    "startDate": "2024-03-01",
                                    "endDate": "2024-07-31",
                                    "totalRounds": 10
                                },
                                {
                                    "id": 2,
                                    "termName": "2024년 하반기",
                                    "startDate": "2024-09-01",
                                    "endDate": "2025-02-28",
                                    "totalRounds": 12
                                }
                            ]
                            """)
                                        }))
            })
    @GetMapping("/rounds")
    ResponseEntity<List<TermSummaryResponse>> getTermList();

    @Operation(
            summary = "챌린지 기간 및 회차 요약 조회",
            description = "특정 챌린지 기간의 상세 정보와 회차 목록을 조회합니다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "챌린지 기간 및 회차 요약 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                TermRoundsSummaryResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "챌린지 기간 및 회차 요약",
                                                    value =
                                                            """
                            {
                                "id": 1,
                                "termName": "2024년 상반기",
                                "startDate": "2024-03-01",
                                "endDate": "2024-07-31",
                                "rounds": [
                                    {
                                        "id": 1,
                                        "name": "1회차",
                                        "period": "2024-03-01 - 2024-03-14"
                                    },
                                    {
                                        "id": 2,
                                        "name": "2회차",
                                        "period": "2024-03-15 - 2024-03-28"
                                    }
                                ]
                            }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @GetMapping("/terms/{termId}/summary")
    ResponseEntity<TermRoundsSummaryResponse> getTermRoundsSummary(
            @Parameter(description = "챌린지 기간 ID", example = "1") @PathVariable Long termId);

    @Operation(
            summary = "챌린지 출석 현황 조회",
            description =
                    """
            챌린지 참여자들의 출석 현황을 조회합니다.

            - termId가 없으면 현재 진행중인 챌린지의 출석 현황을 조회합니다.
            - 각 참여자별로 회차별 출석 횟수를 확인할 수 있습니다.
            """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "출석 현황 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                AttendanceStatusResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "출석 현황 조회 결과",
                                                    value =
                                                            """
                            [
                                {
                                    "userId": 1,
                                    "userName": "홍길동",
                                    "sequence": [2, 1, 3, 0, 2],
                                    "totalCount": 8
                                },
                                {
                                    "userId": 2,
                                    "userName": "김철수",
                                    "sequence": [1, 2, 1, 1, 1],
                                    "totalCount": 6
                                }
                            ]
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "404",
                        description = "챌린지 기간을 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 챌린지 기간",
                                                    value =
                                                            """
                            {
                                "message": "존재하지 않는 챌린지 기간입니다."
                            }
                            """)
                                        }))
            })
    @GetMapping("/terms/attendance")
    ResponseEntity<List<AttendanceStatusResponse>> getAttendanceStatus(
            @Parameter(description = "챌린지 기간 ID (없으면 현재 진행중인 챌린지)", example = "1")
                    @RequestParam(value = "termId", required = false)
                    Long termId);
}
