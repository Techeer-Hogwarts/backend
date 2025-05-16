package backend.techeerzip.domain.projectMember.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.global.entity.StatusCategory;

@SpringBootTest
class ProjectMemberTest {

    @Autowired private ProjectMemberRepository projectMemberRepository;

    @Test
    void jpaIsDeletedTrueThenFalseTest() {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        10L, 1L, StatusCategory.APPROVED);
        Assertions.assertFalse(isMember);
    }

    @Test
    void jpaIsDeletedFalseThenTrueTest() {
        final boolean isMember =
                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                        7L, 1L, StatusCategory.APPROVED);
        Assertions.assertTrue(isMember);
    }
}
