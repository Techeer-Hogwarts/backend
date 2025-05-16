package backend.techeerzip.domain.projectTeam.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.entity.StackCategory;
import backend.techeerzip.domain.stack.repository.StackRepository;

@ActiveProfiles("test")
@DataJpaTest
class ProjectTeamStackRepositoryTest {

    @Autowired ProjectTeamStackRepository projectTeamStackRepository;
    @Autowired ProjectTeamRepository projectTeamRepository;
    @Autowired StackRepository stackRepository;

    private ProjectTeam savedTeam;
    private Stack savedStack;

    @BeforeEach
    void setup() {
        savedTeam =
                projectTeamRepository.save(
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
                                .build());
        savedStack =
                stackRepository.save(
                        Stack.builder().category(StackCategory.BACKEND).name("").build());
    }

    @Test
    void deleteAllByProjectTeam() {
        final TeamStack stack1 =
                TeamStack.builder().stack(savedStack).projectTeam(savedTeam).isMain(true).build();
        final TeamStack stack2 =
                TeamStack.builder().stack(savedStack).projectTeam(savedTeam).isMain(true).build();
        projectTeamStackRepository.saveAll(List.of(stack1, stack2));
        projectTeamStackRepository.deleteAllByProjectTeam(savedTeam);
        final List<TeamStack> expect = projectTeamStackRepository.findAll();
        Assertions.assertTrue(expect.isEmpty());
    }
}
