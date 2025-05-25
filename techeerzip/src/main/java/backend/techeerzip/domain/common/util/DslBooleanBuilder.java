package backend.techeerzip.domain.common.util;

import java.util.function.Function;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

public class DslBooleanBuilder {

    private BooleanExpression expr = Expressions.TRUE;

    public static DslBooleanBuilder builder() {
        return new DslBooleanBuilder();
    }

    public DslBooleanBuilder and(BooleanExpression other) {
        if (other != null) {
            expr = expr.and(other);
        }
        return this;
    }

    public <T> DslBooleanBuilder andIfNotNull(T val, Function<T, BooleanExpression> fn) {
        if (val != null) {
            expr = expr.and(fn.apply(val));
        }
        return this;
    }

    public DslBooleanBuilder andIfNotNull(BooleanExpression cond) {
        if (cond != null) {
            expr = expr.and(cond);
        }
        return this;
    }

    public BooleanExpression build() {
        return expr;
    }
}
