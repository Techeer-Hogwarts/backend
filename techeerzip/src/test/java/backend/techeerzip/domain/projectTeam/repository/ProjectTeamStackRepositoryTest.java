// package backend.techeerzip.domain.projectTeam.repository;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ActiveProfiles;
//
// import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
// import backend.techeerzip.domain.stack.entity.Stack;
// import backend.techeerzip.domain.stack.entity.StackCategory;
// import backend.techeerzip.domain.stack.repository.StackRepository;
//
// @ActiveProfiles("test")
// @DataJpaTest
// class ProjectTeamStackRepositoryTest {
//
//    @Autowired ProjectTeamStackRepository projectTeamStackRepository;
//    @Autowired ProjectTeamRepository projectTeamRepository;
//    @Autowired StackRepository stackRepository;
//
//    private ProjectTeam savedTeam;
//    private Stack savedStack;
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
//        savedStack =
//                stackRepository.save(
//                        Stack.builder().category(StackCategory.BACKEND).name("").build());
//    }
// }
