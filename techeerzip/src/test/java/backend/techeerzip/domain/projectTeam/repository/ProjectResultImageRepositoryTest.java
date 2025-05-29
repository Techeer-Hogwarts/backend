//package backend.techeerzip.domain.projectTeam.repository;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
//import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
//
//@ActiveProfiles("test")
//@DataJpaTest
//class ProjectResultImageRepositoryTest {
//
//    @Autowired private ProjectTeamRepository projectTeamRepository;
//    @Autowired private ProjectResultImageRepository projectResultImageRepository;
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
//    void countByProjectTeamId() {
//        final ProjectResultImage img1 =
//                ProjectResultImage.builder().projectTeam(savedTeam).imageUrl("").build();
//        final ProjectResultImage img2 =
//                ProjectResultImage.builder().projectTeam(savedTeam).imageUrl("").build();
//
//        projectResultImageRepository.saveAll(List.of(img1, img2));
//
//        Assertions.assertEquals(
//                projectResultImageRepository.countByProjectTeamId(savedTeam.getId()), 2);
//    }
//}
