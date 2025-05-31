package backend.techeerzip.domain.session.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 목록 조회 요청 DTO")
public record SessionListQueryRequest(
        @Parameter(description = "카테고리", example = "STUDY")
                @Schema(description = "카테고리", nullable = true)
                String category,
        @Parameter(
                        description = "기간 목록 (예: ?date=WINTER_2024&date=SUMMER_2024)",
                        array = @ArraySchema(schema = @Schema(implementation = String.class)),
                        style = ParameterStyle.FORM,
                        explode = Explode.TRUE)
                @Schema(description = "기간 목록", nullable = true)
                List<String> date,
        @Parameter(
                        description = "포지션 목록 (예: ?position=BACKEND&position=FRONTEND)",
                        array = @ArraySchema(schema = @Schema(implementation = String.class)),
                        style = ParameterStyle.FORM,
                        explode = Explode.TRUE)
                @Schema(description = "포지션 목록", nullable = true)
                List<String> position,
        @Parameter(description = "가져올 데이터 수 (기본값: 10)", example = "10")
                @Schema(description = "가져올 데이터 수 (기본값: 10)", example = "10", nullable = true)
                @Min(value = 1, message = "size는 1 이상이어야 합니다")
                Integer size,
        @Parameter(description = "마지막으로 조회된 데이터의 ID (커서)", example = "10")
                @Schema(description = "마지막으로 조회된 데이터의 ID (커서)", example = "10", nullable = true)
                @Positive(message = "cursor는 양수여야 합니다")
                Long cursor,
        @Parameter(description = "마지막으로 조회된 데이터의 생성일시", example = "2025-04-10T04:06:00")
                @Schema(
                        description = "마지막으로 조회된 데이터의 생성일시",
                        example = "2025-04-10T04:06:00",
                        nullable = true)
                LocalDateTime createdAt) {}
