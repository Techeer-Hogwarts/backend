package backend.techeerzip.domain.projectTeam.type;

import java.util.List;

import org.springframework.cglib.core.internal.Function;

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

    public static PositionNumType from(PositionType position) {
        return PositionNumType.valueOf(position.name());
    }

    public static List<PositionNumType> fromMany(List<PositionType> positions) {
        if (positions == null || positions.isEmpty()) {
            return List.of();
        }
        return positions.stream().map(pos -> PositionNumType.valueOf(pos.name())).toList();
    }

    public NumberExpression<Integer> getField(QProjectTeam pt) {
        return fieldExtractor.apply(pt);
    }
}
