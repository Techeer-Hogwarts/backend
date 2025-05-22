package backend.techeerzip.domain.projectTeam.type;

import java.util.List;

import org.springframework.cglib.core.internal.Function;

import com.querydsl.core.types.dsl.NumberExpression;

import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;

/**
 * 프로젝트 팀 내 각 포지션에 대한 인원수를 조회하기 위한 QueryDsl 열거형입니다. 각 포지션 타입은 해당 포지션의 인원수를 나타내는 QProjectTeam 필드 추출기와
 * 연결되어 있습니다.
 */
public enum PositionNumType {
    FRONTEND(pt -> pt.frontendNum),
    BACKEND(pt -> pt.backendNum),
    DEVOPS(pt -> pt.devopsNum),
    FULLSTACK(pt -> pt.fullStackNum),
    DATA_ENGINEER(pt -> pt.dataEngineerNum);

    private final Function<QProjectTeam, NumberExpression<Integer>> fieldExtractor;

    PositionNumType(Function<QProjectTeam, NumberExpression<Integer>> fieldExtractor) {
        this.fieldExtractor = fieldExtractor;
    }

    /**
     * PositionType을 PositionNumType으로 변환합니다.
     *
     * @param position 변환할 PositionType
     * @return 변환된 PositionNumType
     */
    public static PositionNumType from(PositionType position) {
        return PositionNumType.valueOf(position.name());
    }

    /**
     * PositionType을 PositionNumType으로 변환합니다.
     *
     * @param positions 변환할 PositionType
     * @return 변환된 PositionNumType
     */
    public static List<PositionNumType> fromMany(List<PositionType> positions) {
        if (positions == null || positions.isEmpty()) {
            return List.of();
        }
        return positions.stream().map(pos -> PositionNumType.valueOf(pos.name())).toList();
    }

    /**
     * QProjectTeam에서 해당 포지션의 인원수를 나타내는 NumberExpression을 반환합니다.
     *
     * @param pt 쿼리 대상 QProjectTeam 인스턴스
     * @return 해당 포지션의 인원수를 나타내는 NumberExpression
     */
    public NumberExpression<Integer> getField(QProjectTeam pt) {
        return fieldExtractor.apply(pt);
    }
}
