package backend.techeerzip.domain.projectTeam.repository.teamStack;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectTeam.entity.QTeamStack;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.stack.entity.QStack;

@Repository
public class ProjectTeamStackDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectTeamStackDslRepository {

    private static final QTeamStack teamStack = QTeamStack.teamStack;
    private static final QStack stack = QStack.stack;

    public ProjectTeamStackDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(TeamStack.class, em, factory);
    }

    public List<TeamStack> findTeamStackByProjectTeamId(Long projectTeamId) {
        return selectFrom(teamStack)
                .leftJoin(teamStack.stack, stack).fetchJoin()
                .where(teamStack.projectTeam.id.eq(projectTeamId))
                .fetch();
    }
}
