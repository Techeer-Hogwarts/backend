package backend.techeerzip.domain.projectTeam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import backend.techeerzip.domain.common.dto.IndexRequest;
import backend.techeerzip.domain.common.dto.SlackRequest;
import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectMember.exception.ProjectInvalidActiveRequester;
import backend.techeerzip.domain.projectMember.exception.ProjectMemberNotFoundException;
import backend.techeerzip.domain.projectMember.repository.ProjectMemberRepository;
import backend.techeerzip.domain.projectMember.service.ProjectMemberService;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo.WithStack;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.exception.ProjectDuplicateTeamName;
import backend.techeerzip.domain.projectTeam.exception.ProjectExceededResultImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectInvalidProjectMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingLeaderException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMissingUpdateMemberException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamNotFoundException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamPositionClosedException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamRecruitmentClosedException;
import backend.techeerzip.domain.projectTeam.repository.ProjectResultImageRepository;
import backend.techeerzip.domain.projectTeam.repository.ProjectTeamRepository;
import backend.techeerzip.domain.projectTeam.repository.querydsl.ProjectTeamDslRepository;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
import backend.techeerzip.domain.role.entity.Role;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.entity.StackCategory;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.entity.StatusCategory;
import backend.techeerzip.global.logger.CustomLogger;

@ExtendWith(MockitoExtension.class)
class ProjectTeamServiceTest {

    @Mock private ProjectMemberService projectMemberService;
    @Mock private ProjectTeamRepository projectTeamRepository;
    @Mock private ProjectTeamDslRepository projectTeamDslRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private ProjectResultImageRepository resultImageRepository;
    @Mock private TeamStackService teamStackService;
    @Mock private UserRepository userRepository;
    @Mock private CustomLogger logger;
    @InjectMocks private ProjectTeamService projectTeamService;

    private ProjectTeam mockTeam;
    private TeamData mockTeamData;
    private RecruitCounts mockRecruitCounts;
    private List<TeamStackInfo.WithName> mockTeamStacks;
    private List<ProjectMemberInfoRequest> projectMemberInfoRequests;

    @BeforeEach
    void setUp() {
        mockTeamData =
                TeamData.builder()
                        .name("name")
                        .githubLink("")
                        .projectExplain("")
                        .isRecruited(true)
                        .isFinished(true)
                        .build();

        mockRecruitCounts =
                RecruitCounts.builder()
                        .backendNum(1)
                        .frontedNum(1)
                        .fullStackNum(1)
                        .devOpsNum(1)
                        .dataEngineerNum(1)
                        .build();
        mockTeamStacks = List.of(new TeamStackInfo.WithName("", true));
        projectMemberInfoRequests =
                List.of(new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND));

        mockTeam =
                ProjectTeam.builder()
                        .name("name")
                        .projectExplain("pje")
                        .frontendNum(1)
                        .backendNum(1)
                        .fullStackNum(1)
                        .devopsNum(1)
                        .dataEngineerNum(1)
                        .isFinished(false)
                        .isRecruited(true)
                        .build();
        final ClassPathResource image = new ClassPathResource("IMG_2326.jpg");
    }

    /* TestCase
     * 1. 팀 생성 성공
     * 2. 중복 이름 발생시 ProjectDuplicateTeamName 예외 발생
     * 3. 존재하는 회원이 아니면 ProjectInvalidProjectMemberException 예외 발생
     * */
    @Nested
    class CreateTest {

        private ProjectTeamCreateRequest mockCreateRequest;

        @BeforeEach
        void setup() {
            mockCreateRequest =
                    ProjectTeamCreateRequest.builder()
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .recruitCounts(mockRecruitCounts)
                            .build();
        }

        @Test
        @DisplayName("프로젝트 생성에 성공한다.")
        void SuccessCreateProject() {
            final Stack mockStack =
                    Stack.builder().name("name").category(StackCategory.BACKEND).build();
            final TeamStackInfo.WithStack mockTeamStack = new WithStack(mockStack, true);
            final User mockUser = User.builder().name("name").email("name").build();
            final User spyUser = Mockito.spy(mockUser);
            Mockito.when(spyUser.getId()).thenReturn(1L);
            when(projectTeamRepository.existsByName("name")).thenReturn(false);
            when(userRepository.findAllById(any())).thenReturn(List.of(spyUser));
            when(teamStackService.create(any())).thenReturn(List.of(mockTeamStack));
            final ProjectTeam spyTeam = Mockito.spy(mockTeam);
            Mockito.when(spyTeam.getId()).thenReturn(1L);
            when(projectTeamRepository.save(any())).thenReturn(spyTeam);

            assertEquals(
                    projectTeamService.create(List.of(""), List.of(""), mockCreateRequest),
                    new ProjectTeamCreateResponse(1L, new SlackRequest(), new IndexRequest()));
        }

        @Test
        @DisplayName("존재하는 프로젝트 이름이면 예외 발생한다.")
        void duplicateNameThenThrow() {
            when(projectTeamRepository.existsByName("name")).thenReturn(true);
            assertThrows(
                    ProjectDuplicateTeamName.class,
                    () -> projectTeamService.create(List.of(""), List.of(""), mockCreateRequest));
        }

        @Test
        @DisplayName("존재하는 프로젝트 이름이면 예외 발생한다.")
        void InvalidUsersThenThrow() {
            final Stack mockStack =
                    Stack.builder().name("name").category(StackCategory.BACKEND).build();
            final TeamStackInfo.WithStack mockTeamStack = new WithStack(mockStack, true);
            when(userRepository.findAllById(any())).thenReturn(List.of());
            when(projectTeamRepository.existsByName("name")).thenReturn(false);
            when(teamStackService.create(any())).thenReturn(List.of(mockTeamStack));

            assertThrows(
                    ProjectInvalidProjectMemberException.class,
                    () -> projectTeamService.create(List.of(""), List.of(""), mockCreateRequest));
        }
    }

    @Nested
    class updateTest {

        ProjectMember pm;
        private ProjectTeamUpdateRequest mockUpdateRequest;
        private ProjectTeam mockPT;
        private User mockU;

        @BeforeEach
        void setup() {
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of(1L))
                            .deleteMembers(List.of(1L))
                            .deleteResultImages(List.of(1L))
                            .build();

            mockPT =
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
            mockU =
                    User.builder()
                            .name("")
                            .email("")
                            .grade("")
                            .isLft(true)
                            .isAuth(true)
                            .mainPosition("")
                            .password("")
                            .githubUrl("")
                            .mediumUrl("")
                            .profileImage("")
                            .velogUrl("")
                            .year(1)
                            .role(new Role(""))
                            .school("")
                            .build();
            pm =
                    ProjectMember.builder()
                            .projectTeam(mockPT)
                            .teamRole(TeamRole.BACKEND)
                            .status(StatusCategory.APPROVED)
                            .summary("")
                            .isLeader(true)
                            .user(mockU)
                            .build();
        }

        @Test
        void InvalidProjectMemberThenThrow() {
            List<ProjectMemberInfoRequest> noLeaderProjectMemberInfoRequests =
                    List.of(
                            new ProjectMemberInfoRequest(1L, false, TeamRole.BACKEND),
                            new ProjectMemberInfoRequest(2L, false, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(noLeaderProjectMemberInfoRequests)
                            .deleteMainImages(List.of(1L))
                            .deleteMembers(List.of(1L))
                            .deleteResultImages(List.of(1L))
                            .build();
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);

            assertThrows(
                    ProjectTeamMissingLeaderException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        @Test
        void exceedResultImageThenThrow() {
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(12);

            assertThrows(
                    ProjectExceededResultImageException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        // 업데이트 되는 멤버가 중복이면 ProjectInvalidProjectMemberException
        @Test
        void duplicateUpdateMemberThenThrow() {
            projectMemberInfoRequests =
                    List.of(
                            new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND),
                            new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of())
                            .deleteMembers(List.of())
                            .deleteResultImages(List.of())
                            .build();
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(1);

            assertThrows(
                    ProjectInvalidProjectMemberException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        // 삭제되는 멤버가 중복이면 ProjectInvalidProjectMemberException
        @Test
        void duplicateDeleteMemberThenThrow() {
            projectMemberInfoRequests =
                    List.of(new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of())
                            .deleteMembers(List.of(1L, 1L))
                            .deleteResultImages(List.of())
                            .build();
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(1);

            assertThrows(
                    ProjectInvalidProjectMemberException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        // 삭제되는 멤버가 다시 삭제 되면 ProjectMemberNotFoundException
        @Test
        void alreadyDeletedMemberThenThrow() {
            projectMemberInfoRequests =
                    List.of(
                            new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND),
                            new ProjectMemberInfoRequest(2L, true, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of())
                            .deleteMembers(List.of(3L))
                            .deleteResultImages(List.of())
                            .build();
            final ProjectMember spyPm = Mockito.spy(pm);
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(1);
            Mockito.when(spyPm.isDeleted()).thenReturn(true);
            Mockito.when(spyPm.getId()).thenReturn(3L);
            when(projectMemberRepository.findAllByProjectTeamId(any())).thenReturn(List.of(spyPm));

            assertThrows(
                    ProjectMemberNotFoundException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        // 삭제되는 멤버와 업데이트 멤버가 중복되면 ProjectTeamDuplicateDeleteUpdateException
        @Test
        void duplicateUpdateDeleteMemberThenThrow() {
            projectMemberInfoRequests =
                    List.of(
                            new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND),
                            new ProjectMemberInfoRequest(2L, true, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of())
                            .deleteMembers(List.of(1L))
                            .deleteResultImages(List.of())
                            .build();
            final ProjectMember spyPm = Mockito.spy(pm);
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(1);
            Mockito.when(spyPm.isDeleted()).thenReturn(true);
            Mockito.when(spyPm.getId()).thenReturn(1L);
            when(projectMemberRepository.findAllByProjectTeamId(any())).thenReturn(List.of(spyPm));

            assertThrows(
                    ProjectMemberNotFoundException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }

        // 삭제되는 멤버가 existingMembers에 없으면 ProjectTeamMissingUpdateMemberException
        @Test
        void notExistDeleteMemberInExistingMemberThenThrow() {
            projectMemberInfoRequests =
                    List.of(
                            new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND),
                            new ProjectMemberInfoRequest(2L, true, TeamRole.BACKEND));
            mockUpdateRequest =
                    ProjectTeamUpdateRequest.builder()
                            .recruitCounts(mockRecruitCounts)
                            .teamData(mockTeamData)
                            .teamStacks(mockTeamStacks)
                            .projectMember(projectMemberInfoRequests)
                            .deleteMainImages(List.of())
                            .deleteMembers(List.of(3L))
                            .deleteResultImages(List.of())
                            .build();

            final ProjectMember spyPm = Mockito.spy(pm);
            when(projectMemberRepository.existsByUserIdAndProjectTeamIdAndIsDeletedFalseAndStatus(
                            any(), any(), any()))
                    .thenReturn(true);
            when(projectTeamRepository.existsByName(any())).thenReturn(false);

            when(projectTeamRepository.findById(any())).thenReturn(Optional.of(mockTeam));
            when(resultImageRepository.countByProjectTeamId(any())).thenReturn(1);
            Mockito.when(spyPm.getId()).thenReturn(1L);
            when(projectMemberRepository.findAllByProjectTeamId(any())).thenReturn(List.of(spyPm));

            assertThrows(
                    ProjectTeamMissingUpdateMemberException.class,
                    () ->
                            projectTeamService.update(
                                    1L, 1L, List.of(), List.of(), mockUpdateRequest));
        }
    }

    @Nested
    @DisplayName("apply 메서드 테스트")
    class ApplyTest {

        private ProjectTeamApplyRequest request;

        @BeforeEach
        void setup() {
            request = new ProjectTeamApplyRequest(1L, TeamRole.BACKEND, "자기소개입니다.");
        }

        @Test
        @DisplayName("모집 중인 포지션이면 지원 성공")
        void applySuccess() {
            ProjectTeam team = Mockito.spy(mockTeam);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(team.isRecruited()).thenReturn(true);
            when(team.isRecruitPosition(TeamRole.BACKEND)).thenReturn(true);
            when(projectMemberService.applyApplicant(any(), any(), any(), any()))
                    .thenReturn(mock(ProjectMember.class));

            SlackRequest result = projectTeamService.apply(request, 10L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("모집이 종료된 팀이면 예외 발생")
        void applyToClosedRecruitmentThenThrow() {
            ProjectTeam team = Mockito.spy(mockTeam);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(team.isRecruited()).thenReturn(false);

            assertThrows(
                    ProjectTeamRecruitmentClosedException.class,
                    () -> projectTeamService.apply(request, 10L));
        }

        @Test
        @DisplayName("포지션이 닫힌 경우 예외 발생")
        void applyToClosedPositionThenThrow() {
            ProjectTeam team = Mockito.spy(mockTeam);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(team.isRecruited()).thenReturn(true);
            when(team.isRecruitPosition(TeamRole.BACKEND)).thenReturn(false);

            assertThrows(
                    ProjectTeamPositionClosedException.class,
                    () -> projectTeamService.apply(request, 10L));
        }

        @Test
        @DisplayName("존재하지 않는 팀이면 예외 발생")
        void applyToNonexistentTeamThenThrow() {
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(
                    ProjectTeamNotFoundException.class,
                    () -> projectTeamService.apply(request, 10L));
        }
    }

    @Nested
    @DisplayName("cancelApplication 메서드 테스트")
    class CancelApplicationTest {

        @Test
        @DisplayName("지원 취소 성공")
        void cancelSuccess() {
            ProjectMember pm = Mockito.mock(ProjectMember.class);
            User user = Mockito.mock(User.class);
            when(user.getEmail()).thenReturn("test@email.com");
            when(pm.getUser()).thenReturn(user);
            when(projectMemberRepository.findByProjectTeamIdAndUserId(1L, 100L))
                    .thenReturn(Optional.of(pm));

            SlackRequest result = projectTeamService.cancelApplication(1L, 100L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("지원 정보가 존재하지 않으면 예외 발생")
        void cancelWithoutExistMemberThenThrow() {
            when(projectMemberRepository.findByProjectTeamIdAndUserId(1L, 100L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ProjectMemberNotFoundException.class,
                    () -> projectTeamService.cancelApplication(1L, 100L));
        }
    }

    @Nested
    @DisplayName("acceptApplicant 메서드 테스트")
    class AcceptApplicantTest {

        @Test
        @DisplayName("정상 요청이면 승인 성공")
        void acceptSuccess() {
            ProjectTeam team = Mockito.mock(ProjectTeam.class);
            when(team.hasUserId(10L)).thenReturn(true);
            when(projectTeamRepository.getReferenceById(1L)).thenReturn(team);

            Assertions.assertDoesNotThrow(() -> projectTeamService.acceptApplicant(1L, 10L, 100L));
        }

        @Test
        @DisplayName("권한 없는 사용자면 예외 발생")
        void notActiveMemberThenThrow() {
            ProjectTeam team = Mockito.mock(ProjectTeam.class);
            when(team.hasUserId(10L)).thenReturn(false);
            when(projectTeamRepository.getReferenceById(1L)).thenReturn(team);

            assertThrows(
                    ProjectInvalidActiveRequester.class,
                    () -> projectTeamService.acceptApplicant(1L, 10L, 100L));
        }
    }

    @Nested
    @DisplayName("rejectApplicant 메서드 테스트")
    class RejectApplicantTest {

        @Test
        @DisplayName("정상 요청이면 거절 성공")
        void rejectSuccess() {
            ProjectTeam team = Mockito.mock(ProjectTeam.class);
            when(team.hasUserId(10L)).thenReturn(true);
            when(projectTeamRepository.getReferenceById(1L)).thenReturn(team);

            Assertions.assertDoesNotThrow(() -> projectTeamService.rejectApplicant(1L, 10L, 100L));
        }

        @Test
        @DisplayName("권한 없는 사용자면 예외 발생")
        void rejectWithoutAuthorityThenThrow() {
            ProjectTeam team = Mockito.mock(ProjectTeam.class);
            when(team.hasUserId(10L)).thenReturn(false);
            when(projectTeamRepository.getReferenceById(1L)).thenReturn(team);

            assertThrows(
                    ProjectInvalidActiveRequester.class,
                    () -> projectTeamService.rejectApplicant(1L, 10L, 100L));
        }
    }

    @Nested
    @DisplayName("updateViewCountAndGetDetail 테스트")
    class UpdateViewCountAndGetDetailTest {

        @Test
        void success() {
            ProjectTeam mockTeam = mock(ProjectTeam.class);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(mockTeam));

            ProjectTeamDetailResponse response = projectTeamService.updateViewCountAndGetDetail(1L);
            assertNotNull(response);
            verify(mockTeam).increaseViewCount();
        }

        @Test
        void notFoundThenThrow() {
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(Exception.class, () -> projectTeamService.updateViewCountAndGetDetail(1L));
        }
    }

    @Nested
    @DisplayName("getYoungTeamsById 테스트")
    class GetYoungTeamsByIdTest {

        @Test
        void success() {
            when(projectTeamDslRepository.findManyYoungTeamById(any(), any(), any()))
                    .thenReturn(List.of(mock(ProjectTeamGetAllResponse.class)));
            List<ProjectTeamGetAllResponse> result =
                    projectTeamService.getYoungTeamsById(List.of(1L), true, false);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getYoungTeams 테스트")
    class GetYoungTeamsTest {

        @Test
        void success() {
            when(projectTeamDslRepository.sliceYoungTeam(any(), any(), any(), any()))
                    .thenReturn(List.of(mock(ProjectTeamGetAllResponse.class)));
            List<ProjectTeamGetAllResponse> result =
                    projectTeamService.getYoungTeams(
                            List.of(PositionNumType.BACKEND), true, false, 10L);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("close 테스트")
    class CloseTest {

        @Test
        void success() {
            ProjectTeam team = mock(ProjectTeam.class);
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(true);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(team));

            projectTeamService.close(1L, 10L);

            verify(team).close();
        }

        @Test
        void notMemberThenThrow() {
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(false);
            assertThrows(
                    ProjectInvalidActiveRequester.class, () -> projectTeamService.close(1L, 10L));
        }

        @Test
        void notFoundThenThrow() {
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(true);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(
                    ProjectTeamNotFoundException.class, () -> projectTeamService.close(1L, 10L));
        }
    }

    @Nested
    @DisplayName("softDelete 테스트")
    class SoftDeleteTest {

        @Test
        void success() {
            ProjectTeam team = mock(ProjectTeam.class);
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(true);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.of(team));

            projectTeamService.softDelete(1L, 10L);

            verify(team).softDelete();
        }

        @Test
        void notMemberThenThrow() {
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(false);
            assertThrows(
                    ProjectInvalidActiveRequester.class,
                    () -> projectTeamService.softDelete(1L, 10L));
        }

        @Test
        void notFoundThenThrow() {
            when(projectMemberService.checkActiveMemberByTeamAndUser(1L, 10L)).thenReturn(true);
            when(projectTeamRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(
                    ProjectTeamNotFoundException.class,
                    () -> projectTeamService.softDelete(1L, 10L));
        }
    }
}
