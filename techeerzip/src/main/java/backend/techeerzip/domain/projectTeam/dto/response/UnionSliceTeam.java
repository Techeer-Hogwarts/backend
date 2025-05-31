package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커서 기반 통합 페이징을 위한 UnionView에서 조회한 팀 정보 요약 응답 DTO입니다.
 *
 * <p>이 객체는 TeamUnionView에서 조회된 결과 중 커서 페이징 기준 필드만 포함합니다. 실제 상세 팀 정보는 이 ID와 teamType을 기반으로 다시 조회됩니다.
 *
 * <p>정렬 기준에 따라 커서로 사용하는 필드가 아래처럼 달라집니다:
 *
 * <ul>
 *   <li>{@code UPDATE_AT_DESC} → {@code updatedAt}, {@code id}
 *   <li>{@code VIEW_COUNT_DESC} → {@code viewCount}, {@code id}
 *   <li>{@code LIKE_COUNT_DESC} → {@code likeCount}, {@code id}
 * </ul>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnionSliceTeam {

    private Long id;
    private TeamType teamType;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer likeCount;
}
