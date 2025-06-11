package backend.techeerzip.domain.stack.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import backend.techeerzip.domain.stack.dto.StackDto;
import backend.techeerzip.domain.stack.dto.StackDto.Response;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Stack", description = "기술 스택 관련 API")
@RequestMapping("/api/v3/stacks")
public interface StackSwagger {

    @Operation(summary = "기술 스택 생성", description = "새로운 기술 스택을 생성합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Void.class))),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 또는 중복된 스택 이름",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    // 이 메서드에 대한 HTTP POST 요청을 처리
    ResponseEntity<Void> createStack(
            @RequestBody( // Swagger 문서에 표시될 요청 본문에 대한 설명입니다.
                            description = "생성 요청 DTO",
                            required = true,
                            content =
                                    @Content(
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    StackDto.Create.class)))
                    StackDto.Create request);

    @Operation(summary = "모든 기술 스택 조회", description = "등록된 모든 기술 스택 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = StackDto.Response.class)))
    @GetMapping
    ResponseEntity<List<Response>> getStacksByCategory();
}
