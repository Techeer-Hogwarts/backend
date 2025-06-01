package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 프로젝트/스터디 통합 커서 페이징 조회 결과 DTO입니다.
 *
 * <p>TeamUnionView를 통해 조회된 페이징 결과를 담으며, 실제 팀 ID와 타입 정보만 담고 상세 응답은 이후 각 도메인 서비스에서 ID 목록을 기반으로
 * 재조회합니다.
 *
 * <p>커서 기반 페이징을 위해 다음 페이지 정보인 {@code sliceNextInfo}를 함께 제공합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * TeamUnionSliceResult result = teamUnionViewDslRepository.fetchSliceBeforeCreatedAtDescCursor(...);
 * List<Long> projectIds = result.getProjectIds();
 * List<Long> studyIds = result.getStudyIds();
 * SliceNextCursor cursor = result.getSliceNextInfo();
 * }</pre>
 */
@Getter
@AllArgsConstructor
public class TeamUnionSliceResult {
    private final List<UnionSliceTeam> unionSliceTeams;
    private final SliceNextCursor sliceNextInfo;

    public List<Long> getIdsByType(TeamType type) {
        return unionSliceTeams.stream()
                .filter(e -> e.getTeamType() == type)
                .map(UnionSliceTeam::getId)
                .toList();
    }

    public List<Long> getProjectIds() {
        return getIdsByType(TeamType.PROJECT);
    }

    public List<Long> getStudyIds() {
        return getIdsByType(TeamType.STUDY);
    }
}
