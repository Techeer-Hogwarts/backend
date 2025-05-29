// package backend.techeerzip.domain.projectMember.repository;
//
// import java.util.List;
//
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ActiveProfiles;
//
// import backend.techeerzip.domain.projectMember.entity.ProjectMember;
// import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
// import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
// import backend.techeerzip.domain.projectTeam.type.TeamRole;
// import backend.techeerzip.domain.role.entity.Role;
// import backend.techeerzip.domain.role.repository.RoleRepository;
// import backend.techeerzip.domain.user.entity.User;
// import backend.techeerzip.domain.user.repository.UserRepository;
// import backend.techeerzip.global.entity.StatusCategory;
//
// @ActiveProfiles("test")
// @DataJpaTest
// @EntityScan(basePackages = "backend.techeerzip.domain")
// class ProjectMemberRepositoryTest {
//
//    @Autowired private ProjectMemberRepository projectMemberRepository;
//    @Autowired private ProjectTeamRepository projectTeamRepository;
//    @Autowired private UserRepository userRepository;
//
//    private ProjectTeam savedTeam;
//    private User user;
//    @Autowired private RoleRepository roleRepository;
//
//    @BeforeEach
//    void setup() {
//        final ProjectTeam mockPT =
//                ProjectTeam.builder()
//                        .projectExplain("")
//                        .name("name")
//                        .recruitExplain("")
//                        .notionLink("")
//                        .isRecruited(true)
//                        .githubLink("")
//                        .fullStackNum(1)
//                        .frontendNum(1)
//                        .devopsNum(1)
//                        .dataEngineerNum(1)
//                        .backendNum(1)
//                        .isFinished(false)
//                        .build();
//        savedTeam = projectTeamRepository.save(mockPT);
//        final User mockU =
//                User.builder()
//                        .name("")
//                        .email("")
//                        .grade("")
//                        .isLft(true)
//                        .isAuth(true)
//                        .mainPosition("")
//                        .password("")
//                        .githubUrl("")
//                        .mediumUrl("")
//                        .profileImage("")
//                        .velogUrl("")
//                        .year(1)
//                        .role(roleRepository.save(new Role("")))
//                        .school("")
//                        .build();
//        user = userRepository.save(mockU);
//    }
//
//    @Test
//    void findByProjectTeamId() {
//        final ProjectMember pm =
//                ProjectMember.builder()
//                        .projectTeam(savedTeam)
//                        .teamRole(TeamRole.BACKEND)
//                        .status(StatusCategory.APPROVED)
//                        .summary("")
//                        .isLeader(true)
//                        .user(user)
//                        .build();
//
//        final ProjectMember saved = projectMemberRepository.save(pm);
//        final ProjectMember find =
//                projectMemberRepository.findByProjectTeamId(savedTeam.getId()).orElseThrow();
//        Assertions.assertEquals(saved, find);
//    }
//
//    @Test
//    void findAllByProjectTeamId() {
//        final ProjectMember pm =
//                ProjectMember.builder()
//                        .projectTeam(savedTeam)
//                        .teamRole(TeamRole.BACKEND)
//                        .status(StatusCategory.APPROVED)
//                        .summary("")
//                        .isLeader(true)
//                        .user(user)
//                        .build();
//        final ProjectMember saved = projectMemberRepository.save(pm);
//        final List<ProjectMember> find =
//                projectMemberRepository.findAllByProjectTeamId(savedTeam.getId());
//        Assertions.assertEquals(saved, find.getFirst());
//    }
//
//    @Test
//    void jpaIsDeletedTrueThenFalseTest() {
//        final ProjectMember pm =
//                ProjectMember.builder()
//                        .projectTeam(savedTeam)
//                        .teamRole(TeamRole.BACKEND)
//                        .status(StatusCategory.APPROVED)
//                        .summary("")
//                        .isLeader(true)
//                        .user(user)
//                        .build();
//        pm.softDelete();
//        projectMemberRepository.save(pm);
//        final ProjectMember deleted =
//                projectMemberRepository.findByProjectTeamId(savedTeam.getId()).orElseThrow();
//        final boolean isMember =
//                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
//                        user.getId(), savedTeam.getId(), StatusCategory.APPROVED);
//        Assertions.assertFalse(isMember);
//        Assertions.assertTrue(deleted.isDeleted());
//    }
//
//    @Test
//    void existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus() {
//        final ProjectMember pm =
//                ProjectMember.builder()
//                        .projectTeam(savedTeam)
//                        .teamRole(TeamRole.BACKEND)
//                        .status(StatusCategory.APPROVED)
//                        .summary("")
//                        .isLeader(true)
//                        .user(user)
//                        .build();
//        projectMemberRepository.save(pm);
//        final boolean isMember =
//                projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
//                        user.getId(), savedTeam.getId(), StatusCategory.APPROVED);
//        Assertions.assertTrue(isMember);
//    }
// }
