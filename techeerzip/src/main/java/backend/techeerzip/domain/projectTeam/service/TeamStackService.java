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
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidTeamStackException;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamStackRepository;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamStackService {

    private final ProjectTeamStackRepository projectTeamStackRepository;
    private final StackRepository stackRepository;

    private static Map<String, Boolean> mapToUpdateInfo(List<WithName> updateTeamStacksRequest) {
        return updateTeamStacksRequest.stream()
                .collect(Collectors.toMap(WithName::getStack, WithName::getIsMain));
    }

    private static void validateStacks(List<Stack> stacks, Map<String, Boolean> updateInfo) {
        if (stacks.size() != updateInfo.size()) {
            throw new ProjectInvalidTeamStackException();
        }
    }

    /**
     * 팀 스택 정보를 기반으로 Stack 엔티티를 조회하고, WithStack DTO로 변환합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>입력된 스택 이름 → isMain 여부 Map 생성
     *   <li>스택 이름 리스트로 Stack 엔티티 조회
     *   <li>조회된 Stack 수와 요청 수가 일치하는지 검증
     *   <li>조회된 Stack과 isMain 정보를 기반으로 WithStack DTO 리스트 생성
     * </ol>
     *
     * @param updateTeamStacksRequest 생성 요청에 포함된 스택 이름 및 isMain 플래그 리스트
     * @return Stack 엔티티와 isMain 정보를 포함한 WithStack DTO 리스트
     * @throws ProjectInvalidTeamStackException 존재하지 않는 스택 이름이 포함되어 있을 경우 (갯수 불일치)
     */
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

    /**
     * 프로젝트 팀의 기존 스택 정보를 삭제하고, 요청된 스택 정보로 갱신합니다.
     *
     * <p><b>처리 순서:</b>
     *
     * <ol>
     *   <li>입력된 스택 이름 → isMain 여부 Map 생성
     *   <li>스택 이름 리스트로 Stack 엔티티 조회
     *   <li>조회된 Stack 수와 요청 수가 일치하는지 검증
     *   <li>기존 프로젝트 팀의 팀 스택 정보 삭제
     *   <li>새로운 Stack + isMain 기반 TeamStack 엔티티 생성 및 저장
     * </ol>
     *
     * @param updateTeamStacksRequest 수정 요청에 포함된 스택 이름 및 isMain 플래그 리스트
     * @param projectTeam 수정 대상 프로젝트 팀 엔티티
     * @throws ProjectInvalidTeamStackException 존재하지 않는 스택 이름이 포함되어 있을 경우 (갯수 불일치)
     */
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
