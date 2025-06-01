package backend.techeerzip.domain.resume.controller;

import backend.techeerzip.domain.resume.dto.request.ResumeCreateRequest;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeResponse;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "resumes", description = "Resume API")
public interface ResumeSwagger {
    @Operation(
        summary = "이력서 생성",
        description = "이력서를 생성합니다.\n\n카테고리는 RESUME, PORTFOLIO, ICT, OTHER 입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 생성 성공",
            content = @Content(schema = @Schema(implementation = ResumeCreateResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    default ResponseEntity<ResumeCreateResponse> createResume(
        @Parameter(hidden = true) @UserId Long userId,
        @RequestPart("file")
        @Parameter(description = "업로드할 이력서 파일") MultipartFile file,
        @RequestPart("request")
        @Parameter(description = "이력서 정보") ResumeCreateRequest request
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "이력서 단일 조회",
        description = "이력서 ID로 단일 이력서를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 조회 성공",
            content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
    })
    default ResponseEntity<ResumeResponse> getResumesByUserId(
        @Parameter(description = "이력서 ID", required = true, example = "1") Long resumeId
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "이력서 삭제",
        description = "단일 이력서를 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "이력서 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
    })
    default ResponseEntity<Void> deleteResume(
        @Parameter(description = "이력서 ID", required = true, example = "1") Long resumeId,
        @Parameter(hidden = true) @UserId Long userId
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "메인 이력서 지정",
        description = "특정 이력서를 메인 이력서로 지정합니다. 기존 메인 이력서는 자동으로 해제됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메인 이력서 지정 성공"),
        @ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    default ResponseEntity<Void> updateMainResume(
        @Parameter(description = "이력서 ID", required = true, example = "1") Long resumeId,
        @Parameter(hidden = true) @UserId Long userId
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "이력서 목록 조회",
        description = "여러 조건(직책, 기수, 카테고리 등)으로 이력서 목록을 조회합니다.\n\n"
            + "직책 예시: BACKEND, FRONTEND, DEVOPS, FULL_STACK, DATA_ENGINEER\n"
            + "카테고리 예시: RESUME, PORTFOLIO, ICT, OTHER"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ResumeResponse.class)))
    })
    default ResponseEntity<List<ResumeResponse>> getResumes(
        @Parameter(
            description = "검색할 직책 (여러 개 가능) - BACKEND, FRONTEND, DEVOPS, FULL_STACK, DATA_ENGINEER",
            example = "[\"BACKEND\", \"FRONTEND\"]",
            required = false
        ) List<String> position,
        @Parameter(
            description = "검색할 기수 (여러 개 가능)",
            example = "[1, 2, 3]",
            required = false
        ) List<Integer> year,
        @Parameter(
            description = "카테고리 - 전체/RESUME/PORTFOLIO/ICT/OTHER (기본값: 전체)",
            example = "OTHER",
            required = false
        ) String category,
        @Parameter(
            description = "커서 ID",
            example = "0",
            required = false
        ) Long cursorId,
        @Parameter(
            description = "가져올 개수 (기본값: 10)",
            example = "10",
            required = false
        ) Integer limit
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "특정 유저의 이력서 목록 조회",
        description = "특정 유저의 id로 이력서 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ResumeResponse.class)))
    })
    default ResponseEntity<List<ResumeResponse>> getUserResumes(
        @Parameter(description = "유저 ID", required = true, example = "1") Long userId
    ) {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }

    @Operation(
        summary = "인기 이력서 목록 조회",
        description = "최근 2주 이내 생성된 이력서 중 (viewCount + likeCount * 10) 내림차순으로 정렬된 인기 이력서 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 이력서 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ResumeResponse.class)))
    })
    default ResponseEntity<List<ResumeResponse>> getBestResumes() {
        throw new UnsupportedOperationException("Swagger 전용 인터페이스입니다.");
    }
}
