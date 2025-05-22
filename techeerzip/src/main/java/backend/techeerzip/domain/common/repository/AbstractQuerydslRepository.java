package backend.techeerzip.domain.common.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.springframework.cglib.core.internal.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public abstract class AbstractQuerydslRepository {

    private final Querydsl querydsl;
    private final JPAQueryFactory queryFactory;

    protected AbstractQuerydslRepository(
            final Class<?> domainClass, EntityManager em, JPAQueryFactory factory) {
        this.queryFactory = factory;
        this.querydsl = new Querydsl(em, new PathBuilderFactory().create(domainClass));
    }

    protected <T> JPAQuery<T> select(final Expression<T> expr) {
        return queryFactory.select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(final EntityPath<T> from) {
        return queryFactory.selectFrom(from);
    }

    protected <T> Page<T> applyPagination(
            final Pageable pageable,
            final Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
            final Function<JPAQueryFactory, JPAQuery<Long>> countQuery) {
        final List<T> content =
                querydsl.applyPagination(pageable, contentQuery.apply(queryFactory)).fetch();
        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> Optional.ofNullable(countQuery.apply(queryFactory).fetchOne()).orElse(0L));
    }

    protected JPAQueryFactory getQueryFactory() {
        return this.queryFactory;
    }
}
