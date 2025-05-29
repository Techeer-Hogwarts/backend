//package backend.techeerzip.domain.projectMember.entity;
//
//@SpringBootTest
//class ProjectMemberTest {
//    private ProjectMember pm;
//
//    @BeforeEach
//    void setup() {
//        final ProjectTeam mockPT =
//                ProjectTeam.builder()
//                        .projectExplain("")
//                        .name("name")
//                        .recruitExplain("")
//                        .notionLink("")
//                        .isRecruited(true)
//                        .githubLink("")
//                        .fullStackNum(1)
//                        .frontendNum(1)
//                        .devopsNum(1)
//                        .dataEngineerNum(1)
//                        .backendNum(1)
//                        .isFinished(false)
//                        .build();
//        final User mockU =
//                User.builder()
//                        .name("")
//                        .email("")
//                        .grade("")
//                        .isLft(true)
//                        .isAuth(true)
//                        .mainPosition("")
//                        .password("")
//                        .githubUrl("")
//                        .mediumUrl("")
//                        .profileImage("")
//                        .velogUrl("")
//                        .year(1)
//                        .role(new Role(""))
//                        .school("")
//                        .build();
//        pm =
//                ProjectMember.builder()
//                        .projectTeam(mockPT)
//                        .teamRole(TeamRole.BACKEND)
//                        .status(StatusCategory.APPROVED)
//                        .summary("")
//                        .isLeader(true)
//                        .user(mockU)
//                        .build();
//    }
//
//    @Test
//    void entityTest() {
//        Assertions.assertFalse(pm.isDeleted());
//        pm.softDelete();
//        Assertions.assertTrue(pm.isDeleted());
//    }
//}
