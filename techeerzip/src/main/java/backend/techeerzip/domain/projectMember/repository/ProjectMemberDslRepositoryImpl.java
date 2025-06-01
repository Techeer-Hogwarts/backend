package backend.techeerzip.domain.projectMember.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.entity.QProjectMember;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.global.entity.StatusCategory;

@Slf4j
@Repository
public class ProjectMemberDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectMemberDslRepository {

    private static final QProjectMember PM = QProjectMember.projectMember;

    public ProjectMemberDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectMember.class, em, factory);
    }

    public List<ProjectMemberApplicantResponse> findManyApplicants(Long teamId) {
        log.info("ProjectMember findManyApplicants: 지원자 조회 시작 - teamId={}", teamId);

        final List<ProjectMemberApplicantResponse> result = select(PM)
                .select(
                        Projections.constructor(
                                ProjectMemberApplicantResponse.class,
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
        log.info("ProjectMember findManyApplicants: 지원자 조회 완료 - resultSize={}", result.size());

        return result;
    }

    public List<LeaderInfo> findManyLeaders(Long teamId) {
        log.info("ProjectMember findManyLeaders: 리더 조회 시작 - teamId={}", teamId);

        final List<LeaderInfo> result = select(PM)
                .select(Projections.constructor(LeaderInfo.class, PM.user.name, PM.user.email))
                .from(PM)
                .where(
                        PM.isLeader.eq(true),
                        PM.isDeleted.eq(false),
                        PM.status.eq(StatusCategory.APPROVED))
                .fetch();

        log.info("ProjectMember findManyLeaders: 리더 조회 완료 - resultSize={}", result.size());
        return result;
    }
}
