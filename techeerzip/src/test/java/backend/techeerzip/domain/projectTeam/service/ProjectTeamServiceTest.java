//package backend.techeerzip.domain.projectTeam.service;
//
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.when;
//import backend.techeerzip.domain.projectMember.dto.ProjectMemberInfoRequest;
//import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
//import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
//import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
//import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
//import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
//import backend.techeerzip.domain.projectTeam.exception.ProjectDuplicateTeamName;
//import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
//import backend.techeerzip.domain.projectTeam.type.TeamRole;
//import backend.techeerzip.domain.stack.entity.Stack;
//import backend.techeerzip.domain.stack.entity.StackCategory;
//import backend.techeerzip.domain.stack.repository.StackRepository;
//import backend.techeerzip.domain.user.entity.User;
//import backend.techeerzip.domain.user.repository.UserRepository;
//import backend.techeerzip.global.logger.CustomLogger;
//import backend.techeerzip.infra.s3.S3Service;
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//@ExtendWith(MockitoExtension.class)
//class ProjectTeamServiceTest {
//
//    ProjectTeamCreateRequest mockRequest;
//    @Mock
//    private TeamStackService projectTeamWriter;
//    @Mock
//    private S3Service s3Service;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private StackRepository stackRepository;
//    @Mock
//    private CustomLogger logger;
//    @InjectMocks
//    private ProjectTeamService projectTeamService;
//    private MultipartFile multipartFile;
//    private ProjectTeam mockTeam;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        final TeamData teamData =
//                TeamData.builder()
//                        .name("name")
//                        .githubLink("")
//                        .projectExplain("")
//                        .isRecruited(true)
//                        .isFinished(true)
//                        .build();
//
//        final RecruitCounts recruitCounts =
//                RecruitCounts.builder()
//                        .backendNum(1)
//                        .frontedNum(1)
//                        .fullStackNum(1)
//                        .devOpsNum(1)
//                        .dataEngineerNum(1)
//                        .build();
//        final TeamStackInfo.WithName teamStackInfoRequest =
//                new TeamStackInfo.WithName("name", true);
//        final ProjectMemberInfoRequest projectMemberInfoRequest =
//                new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND);
//        mockRequest =
//                ProjectTeamCreateRequest.builder()
//                        .teamData(teamData)
//                        .stacks(List.of(1))
//                        .projectMember(List.of(projectMemberInfoRequest))
//                        .teamStacks(List.of(teamStackInfoRequest))
//                        .recruitCounts(recruitCounts)
//                        .build();
//        mockTeam =
//                ProjectTeam.builder()
//                        .name("name")
//                        .projectExplain("pje")
//                        .frontendNum(1)
//                        .backendNum(1)
//                        .fullStackNum(1)
//                        .devopsNum(1)
//                        .dataEngineerNum(1)
//                        .isFinished(false)
//                        .isRecruited(true)
//                        .build();
//        final ClassPathResource image = new ClassPathResource("IMG_2326.jpg");
//        multipartFile =
//                new MockMultipartFile(
//                        "file", "test-image.jpg", "image/jpeg", image.getInputStream());
//    }
//
//    /**
//     * 1. Check total recruit member count - If count == 0, set isRecruit = false 2. Validate
//     * duplicate project name (*READ) - If project name exists, throw an error 3. Validate the
//     * existence of project team leader - If no project team leader exists, throw an error 4.
//     * Extract mainImage and resultImage from files - If mainImage is missing, throw error 5.
//     * Validate project member teamRole - If invalid teamRole exists in project members, throw an
//     * error 6. Fetch teamStacks from StackService by stack name (*READ) - teamStack is nullable -
//     * Match StackResponse names with teamStack names - If teamStack does not exist in StackService,
//     * throw an error - Map StackData (stackId, isMain) 7. Upload mainImage and resultImages to S3
//     * (*TRANSACTION) 8. Create ProjectTeam (*TRANSACTION) - Map to ProjectTeamDetailResponse 9.
//     * Notify via Slack (*TRANSACTION) - Extract project team leader info (name, email) - Map to
//     * CreateProjectAlertRequest - Send to AlertService 10. Indexing (*TRANSACTION) - Send
//     * ProjectTeamDetailResponse to IndexService 11. Return ProjectTeamDetailResponse
//     */
//    @Nested
//    class CreateTest {
//
//        @Test
//        @DisplayName("프로젝트 생성에 성공한다.")
//        void SuccessCreateProject() {
//            Stack mockStack = Stack.builder().name("name").category(StackCategory.BACKEND).build();
//            User mockUser = User.builder().name("name").email("name").build();
//            when(projectTeamReader.checkName(any())).thenReturn(false);
//            when(projectTeamWriter.create(any())).thenReturn(1L);
//            when(userRepository.findById(any())).thenReturn(Optional.ofNullable(mockUser));
//            when(stackRepository.findAllByNameIn(any())).thenReturn(List.of(mockStack));
//            when(s3Service.upload(any(), any(), any())).thenReturn("");
//            when(s3Service.uploadMany(any(), any(), any())).thenReturn(List.of());
//
//            Assertions.assertEquals(
//                    1L,
//                    projectTeamService
//                            .create(
//                                    ProjectTeamMapper.separateImages(List.of(multipartFile)),
//                                    mockRequest)
//                            .getBody());
//        }
//
//        @Test
//        @DisplayName("존재하는 프로젝트 이름이면 예외 발생한다.")
//        void duplicateNameThenThrow() {
//            when(projectTeamReader.checkName(any())).thenReturn(true);
//            Assertions.assertThrows(
//                    ProjectDuplicateTeamName.class,
//                    () ->
//                            projectTeamService.create(
//                                    ProjectTeamMapper.separateImages(List.of(multipartFile)),
//                                    mockRequest));
//        }
//
//        @Test
//        @DisplayName("")
//        void totalRecruitNumIs0ThenIsRecruitedFalse() {
//            final TeamData teamData =
//                    TeamData.builder()
//                            .name("name")
//                            .githubLink("")
//                            .projectExplain("")
//                            .isRecruited(true)
//                            .isFinished(true)
//                            .build();
//            final RecruitCounts recruitCounts =
//                    RecruitCounts.builder()
//                            .backendNum(0)
//                            .frontedNum(0)
//                            .devOpsNum(0)
//                            .dataEngineerNum(0)
//                            .build();
//            final TeamStackInfo.WithName teamStackInfoRequest =
//                    new TeamStackInfo.WithName("name", true);
//            final ProjectMemberInfoRequest projectMemberInfoRequest =
//                    new ProjectMemberInfoRequest(1L, true, TeamRole.BACKEND);
//            final ProjectTeamCreateRequest projectTeamRequest =
//                    ProjectTeamCreateRequest.builder()
//                            .teamData(teamData)
//                            .stacks(List.of(1))
//                            .projectMember(List.of(projectMemberInfoRequest))
//                            .teamStacks(List.of(teamStackInfoRequest))
//                            .recruitCounts(recruitCounts)
//                            .build();
//            projectTeamService.create(
//                    ProjectTeamMapper.separateImages(List.of(multipartFile)), projectTeamRequest);
//        }
//
//        //        @Test
//        //        @DisplayName("file이 없으면 ProjectDuplicateTeamName 예외가 발생한다.")
//        //        void failFile() {
//        //            Assertions.assertThrows(ProjectImageException.class,
//        //                    () -> projectTeamService.create(List.of(), mockRequest)
//        //            );
//        //        }
//    }
//}
