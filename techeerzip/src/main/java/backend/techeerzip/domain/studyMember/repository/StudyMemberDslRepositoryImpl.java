package backend.techeerzip.domain.studyMember.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.studyMember.entity.QStudyMember;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class StudyMemberDslRepositoryImpl extends AbstractQuerydslRepository
        implements StudyMemberDslRepository {
    private static final QStudyMember SM = QStudyMember.studyMember;

    public StudyMemberDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(StudyMember.class, em, factory);
    }

    public List<StudyApplicantResponse> findManyApplicants(Long teamId) {
        log.info(
                "StudyMemberDslRepository findManyApplicants: PENDING 상태 지원자 조회 시작, teamId={}",
                teamId);

        final List<StudyApplicantResponse> result =
                select(SM)
                        .select(
                                Projections.constructor(
                                        StudyApplicantResponse.class,
                                        SM.id,
                                        SM.summary,
                                        SM.status,
                                        SM.user.id,
                                        SM.user.name,
                                        SM.user.profileImage,
                                        SM.user.year))
                        .from(SM)
                        .where(
                                SM.status.eq(StatusCategory.PENDING),
                                SM.studyTeam.id.eq(teamId),
                                SM.isDeleted.eq(false))
                        .fetch();

        log.info("StudyMemberDslRepository findManyApplicants: 조회 완료, 결과 수={}", result.size());
        return result;
    }
}
