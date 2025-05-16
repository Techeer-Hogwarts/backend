package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectMember.entity.QProjectMember;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectUserTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamLeaderAlertData;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.QProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.global.entity.StatusCategory;

@Repository
public class ProjectTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectTeamDslRepository {

    private static final QProjectTeam PT = QProjectTeam.projectTeam;
    private static final QProjectMember PM = QProjectMember.projectMember;
    private static final QProjectMainImage PMI = QProjectMainImage.projectMainImage;

    public ProjectTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectTeam.class, em, factory);
    }

    private static BooleanBuilder setBuilder(Boolean isRecruited, Boolean isFinished) {
        final BooleanBuilder builder = new BooleanBuilder();

        if (isRecruited != null) {
            builder.and(PT.isRecruited.eq(isRecruited));
        }
        if (isFinished != null) {
            builder.and(PT.isFinished.eq(isFinished));
        }
        return builder;
    }

    public List<ProjectTeamGetAllResponse> sliceYoungTeam(
            List<PositionNumType> positionNumType,
            Boolean isRecruited,
            Boolean isFinished,
            Long limit) {
        final BooleanBuilder builder = setBuilder(isRecruited, isFinished);

        if (positionNumType != null && !positionNumType.isEmpty()) {
            final BooleanBuilder positionFilter = new BooleanBuilder();
            for (PositionNumType p : positionNumType) {
                positionFilter.or(p.getField(PT).gt(0));
            }
            builder.and(positionFilter);
        }
        final List<ProjectTeam> teams =
                selectFrom(PT).where(builder).orderBy(PT.createdAt.desc()).limit(limit + 1).fetch();
        return teams.stream().map(ProjectTeamMapper::toGetAllResponse).toList();
    }

    public List<ProjectTeamGetAllResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        final BooleanBuilder builder = setBuilder(isRecruited, isFinished);
        builder.and(PT.id.in(keys));
        final List<ProjectTeam> teams =
                selectFrom(PT).where(builder).orderBy(PT.createdAt.desc()).fetch();
        return teams.stream().map(ProjectTeamMapper::toGetAllResponse).toList();
    }

    public List<TeamLeaderAlertData> findAlertDataForLeader(Long teamId) {
        return select(
                        Projections.constructor(
                                TeamLeaderAlertData.class, PT.id, PT.name, PM.user.email))
                .from(PT)
                .join(PT.projectMembers, PM)
                .where(
                        PT.id.eq(teamId),
                        PT.isDeleted.eq(false),
                        PM.isLeader.eq(true),
                        PM.status.eq(StatusCategory.APPROVED))
                .fetch();
    }

    public List<ProjectUserTeamsResponse> findAllTeamsByUserId(Long userId) {
        return selectFrom(PT)
                .leftJoin(PT.projectMembers, PM)
                .leftJoin(PT.mainImages, PMI)
                .where(
                        PT.isDeleted.eq(false),
                        PM.user.id.eq(userId),
                        PM.isDeleted.eq(false),
                        PM.status.eq(StatusCategory.APPROVED))
                .transform(
                        GroupBy.groupBy(PT.id)
                                .list(
                                        Projections.constructor(
                                                ProjectUserTeamsResponse.class,
                                                PT.id,
                                                PM.user.name,
                                                GroupBy.list(PMI.imageUrl).as("mainImageUrl"))));
    }
}
