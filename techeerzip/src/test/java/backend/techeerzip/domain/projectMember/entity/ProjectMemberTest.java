/*
package backend.techeerzip.domain.projectMember.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;

@SpringBootTest
class ProjectMemberTest {

    @Autowired private ProjectMemberRepository projectMemberRepository;
    @Autowired private ProjectTeamRepository projectTeamRepository;

    @Test
    void update() {
        ProjectTeam projectTeam =
                ProjectTeam.builder()
                        .projectExplain("")
                        .name("1")
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
        ProjectTeam created = projectTeamRepository.save(projectTeam);
    }
}
*/
