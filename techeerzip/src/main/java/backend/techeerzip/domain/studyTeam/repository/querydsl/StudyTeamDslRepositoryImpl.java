package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.studyTeam.entity.QStudyTeam;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;

@Repository
public class StudyTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements StudyTeamDslRepository {

    private static final QStudyTeam STUDY_TEAM = QStudyTeam.studyTeam;

    public StudyTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(StudyTeam.class, em, factory);
    }

    public List<StudyTeamGetAllResponse> fetchStudyTeams(Boolean isRecruited, Boolean isFinished) {
        final BooleanBuilder builder = new BooleanBuilder();

        if (isRecruited != null) {
            builder.and(STUDY_TEAM.isRecruited.eq(isRecruited));
        }
        if (isFinished != null) {
            builder.and(STUDY_TEAM.isFinished.eq(isFinished));
        }

        List<StudyTeam> teams =
                selectFrom(STUDY_TEAM).where(builder).orderBy(STUDY_TEAM.createdAt.desc()).fetch();
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }
}
