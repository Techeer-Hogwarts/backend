package backend.techeerzip.domain.projectTeam.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

@ActiveProfiles("test")
@DataJpaTest
class ProjectTeamRepositoryTest {

    @Autowired private ProjectTeamRepository projectTeamRepository;

    @Nested
    class CreateTest {

        private ProjectTeam savedTeam;

        @BeforeEach
        void setup() {
            savedTeam =
                    ProjectTeam.builder()
                            .projectExplain("")
                            .name("name")
                            .recruitExplain("")
                            .notionLink("")
                            .isRecruited(true)
                            .githubLink("")
                            .fullStackNum(1)
                            .frontendNum(1)
                            .devopsNum(1)
                            .dataEngineerNum(1)
                            .backendNum(1)
                            .isFinished(false)
                            .build();
        }

        @Test
        void createProjectTeamEntity() {
            projectTeamRepository.save(savedTeam);
            Assertions.assertThat(savedTeam.getId()).isEqualTo(1L);
        }

        @Test
        void findByIdProjectTeam() {
            ProjectTeam saved = projectTeamRepository.save(savedTeam);
            ProjectTeam find = projectTeamRepository.findById(1L).orElseThrow();

            Assertions.assertThat(saved.getId()).isEqualTo(find.getId());
        }
    }
}
