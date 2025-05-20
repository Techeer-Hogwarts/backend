package backend.techeerzip.domain.projectMember.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.entity.QProjectMember;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectApplicantResponse;
import backend.techeerzip.global.entity.StatusCategory;

@Repository
public class ProjectMemberDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectMemberDslRepository {

    private static final QProjectMember PM = QProjectMember.projectMember;

    public ProjectMemberDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectMember.class, em, factory);
    }

    public List<ProjectApplicantResponse> findManyApplicants(Long teamId) {
        return select(PM)
                .select(
                        Projections.constructor(
                                ProjectApplicantResponse.class,
                                PM.id,
                                PM.teamRole,
                                PM.summary,
                                PM.status,
                                PM.user.id,
                                PM.user.name,
                                PM.user.profileImage,
                                PM.user.year))
                .from(PM)
                .where(
                        PM.status.eq(StatusCategory.PENDING),
                        PM.projectTeam.id.eq(teamId),
                        PM.isDeleted.eq(false))
                .fetch();
    }

    public List<LeaderInfo> findManyLeaders(Long teamId) {
        return select(PM)
                .select(Projections.constructor(LeaderInfo.class, PM.user.name, PM.user.email))
                .from(PM)
                .where(
                        PM.isLeader.eq(true),
                        PM.isDeleted.eq(false),
                        PM.status.eq(StatusCategory.APPROVED))
                .fetch();
    }
}
