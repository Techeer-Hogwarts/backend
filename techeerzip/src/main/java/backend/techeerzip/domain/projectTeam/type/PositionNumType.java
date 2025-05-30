package backend.techeerzip.domain.projectTeam.type;

import java.util.List;
import java.util.function.Function;

import com.querydsl.core.types.dsl.NumberExpression;

import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;

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
