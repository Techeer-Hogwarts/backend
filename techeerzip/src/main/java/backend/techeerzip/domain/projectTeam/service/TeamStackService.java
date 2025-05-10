package backend.techeerzip.domain.projectTeam.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo.WithName;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamStackRepository;
import backend.techeerzip.domain.projectTeam.repository.teamStack.ProjectTeamStackDslRepository;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamStackService {

    private final ProjectTeamStackDslRepository projectTeamStackDslRepository;
    private final ProjectTeamStackRepository projectTeamStackRepository;
    private final StackRepository stackRepository;

    private static Map<String, Boolean> mapToUpdateInfo(List<WithName> updateTeamStacksRequest) {
        return updateTeamStacksRequest.stream()
                .collect(Collectors.toMap(WithName::getStack, WithName::getIsMain));
    }

    private static void validateStacks(List<Stack> stacks, Map<String, Boolean> updateInfo) {
        if (stacks.size() != updateInfo.size()) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public List<TeamStackInfo.WithStack> create(
            List<TeamStackInfo.WithName> updateTeamStacksRequest) {
        Map<String, Boolean> createInfo = mapToUpdateInfo(updateTeamStacksRequest);
        List<Stack> stacks = getStacks(createInfo);
        validateStacks(stacks, createInfo);
        return stacks.stream()
                .map(
                        s ->
                                TeamStackInfo.WithStack.builder()
                                        .stack(s)
                                        .isMain(createInfo.get(s.getName()))
                                        .build())
                .toList();
    }

    @Transactional
    public void update(
            List<TeamStackInfo.WithName> updateTeamStacksRequest, ProjectTeam projectTeam) {
        Map<String, Boolean> updateInfo = mapToUpdateInfo(updateTeamStacksRequest);
        List<Stack> stacks = getStacks(updateInfo);
        validateStacks(stacks, updateInfo);
        projectTeamStackRepository.deleteAllByProjectTeam(projectTeam);
        List<TeamStack> teamStacks =
                stacks.stream()
                        .map(
                                s ->
                                        TeamStack.builder()
                                                .isMain(updateInfo.get(s.getName()))
                                                .stack(s)
                                                .projectTeam(projectTeam)
                                                .build())
                        .toList();
        projectTeamStackRepository.saveAll(teamStacks);
    }

    private List<Stack> getStacks(Map<String, Boolean> updateInfo) {
        return stackRepository.findAllByNameIn(updateInfo.keySet());
    }
}
