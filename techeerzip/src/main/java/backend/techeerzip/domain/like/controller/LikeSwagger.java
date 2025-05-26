package backend.techeerzip.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import backend.techeerzip.domain.like.dto.request.LikeRequest;
import backend.techeerzip.domain.like.dto.response.LikeResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "likes", description = "좋아요 API")
public interface LikeSwagger {

    @Operation(summary = "좋아요 조회", description = "좋아요 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좋아요 조회 성공"),
        @ApiResponse(responseCode = "404", description = "좋아요 조회 실패")
    })
    default ResponseEntity<LikeResponse> getLike(
        @Parameter(description = "좋아요 조회") @PathVariable Long likeId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
