package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.studyTeam.entity.QStudyTeam;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;

@Repository
public class StudyTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements StudyTeamDslRepository {

    private static final QStudyTeam ST = QStudyTeam.studyTeam;

    public StudyTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(StudyTeam.class, em, factory);
    }

    private static BooleanExpression setBuilder(Boolean isRecruited, Boolean isFinished) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    public List<StudyTeamGetAllResponse> sliceYoungTeam(
            Boolean isRecruited, Boolean isFinished, Long limit) {
        final BooleanExpression expression = setBuilder(isRecruited, isFinished);

        List<StudyTeam> teams =
                selectFrom(ST).where(expression).orderBy(ST.createdAt.desc()).limit(limit).fetch();
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }

    public List<StudyTeamGetAllResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        final BooleanExpression expression =
                setBuilder(isRecruited, isFinished).and(ST.id.in(keys));
        List<StudyTeam> teams =
                selectFrom(ST).where(expression).orderBy(ST.createdAt.desc()).fetch();
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }
}
