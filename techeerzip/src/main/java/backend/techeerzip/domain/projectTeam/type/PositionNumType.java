package backend.techeerzip.domain.projectTeam.type;

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

    public NumberExpression<Integer> getField(QProjectTeam pt) {
        return fieldExtractor.apply(pt);
    }
}
