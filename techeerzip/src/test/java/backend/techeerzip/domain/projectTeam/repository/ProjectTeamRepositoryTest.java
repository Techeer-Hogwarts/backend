// package backend.techeerzip.domain.projectTeam.repository;
//
// import jakarta.persistence.EntityManager;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
//
// import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
//
// @ActiveProfiles("test")
// @DataJpaTest
// class ProjectTeamRepositoryTest {
//
//    @Autowired private ProjectTeamRepository projectTeamRepository;
//
//    @Autowired private EntityManager em;
//
//    private ProjectTeam savedTeam;
//
//    @BeforeEach
//    void setup() {
//        savedTeam =
//                projectTeamRepository.save(
//                        ProjectTeam.builder()
//                                .projectExplain("")
//                                .name("name")
//                                .recruitExplain("")
//                                .notionLink("")
//                                .isRecruited(true)
//                                .githubLink("")
//                                .fullStackNum(1)
//                                .frontendNum(1)
//                                .devopsNum(1)
//                                .dataEngineerNum(1)
//                                .backendNum(1)
//                                .isFinished(false)
//                                .build());
//    }
//
//    @Test
//    void existsByNameTrue() {
//        Assertions.assertThat(projectTeamRepository.existsByName("name")).isTrue();
//    }
//
//    @Test
//    @Transactional
//    void checkViewCountIncrease() {
//        final ProjectTeam pm = projectTeamRepository.findById(savedTeam.getId()).orElseThrow();
//        pm.increaseViewCount();
//        em.flush();
//        em.clear();
//
//        Assertions.assertThat(projectTeamRepository.findById(savedTeam.getId())).isPresent();
//    }
//
//    @Nested
//    class CreateTest {
//
//        @Test
//        void createProjectTeamEntity() {
//            final ProjectTeam create =
//                    projectTeamRepository.save(
//                            ProjectTeam.builder()
//                                    .projectExplain("")
//                                    .name("create")
//                                    .recruitExplain("")
//                                    .notionLink("")
//                                    .isRecruited(true)
//                                    .githubLink("")
//                                    .fullStackNum(1)
//                                    .frontendNum(1)
//                                    .devopsNum(1)
//                                    .dataEngineerNum(1)
//                                    .backendNum(1)
//                                    .isFinished(false)
//                                    .build());
//            Assertions.assertThat(create).isNotNull();
//        }
//
//        @Test
//        void findByIdProjectTeam() {
//            ProjectTeam find = projectTeamRepository.findById(savedTeam.getId()).orElseThrow();
//
//            Assertions.assertThat(savedTeam.getId()).isEqualTo(find.getId());
//        }
//    }
// }
