package backend.techeerzip.domain.resume.controller;

import backend.techeerzip.domain.resume.dto.request.ResumeCreateRequest;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
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
}
