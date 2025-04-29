package backend.techeerzip.domain.projectTeam.entity;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.stack.entity.TeamStack;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_teams")
public class ProjectTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(name = "is_recruited", nullable = false)
    private boolean isRecruited;

    @Column(name = "is_finished", nullable = false)
    private boolean isFinished;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "github_link", nullable = false, length = 500)
    private String githubLink;

    @Column(name = "notion_link", nullable = false, length = 500)
    private String notionLink;

    @Column(name = "project_explain", nullable = false, length = 500)
    private String projectExplain;

    @Column(name = "frontend_num", nullable = false)
    private Integer frontendNum;

    @Column(name = "backend_num", nullable = false)
    private Integer backendNum;

    @Column(name = "devops_num", nullable = false)
    private Integer devopsNum;

    @Column(name = "full_stack_num", nullable = false)
    private Integer fullStackNum;

    @Column(name = "data_engineer_num", nullable = false)
    private Integer dataEngineerNum;

    @Column(name = "recruit_explain", nullable = false, length = 3000)
    private String recruitExplain;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL)
    private List<TeamStack> teamStacks = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL)
    private List<ProjectResultImage> resultImages = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL)
    private List<ProjectMainImage> mainImages = new ArrayList<>();

    @Builder
    public ProjectTeam(boolean isRecruited, boolean isFinished, String name, String githubLink,
                      String notionLink, String projectExplain, Integer frontendNum, Integer backendNum,
                      Integer devopsNum, Integer fullStackNum, Integer dataEngineerNum, String recruitExplain) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isRecruited = isRecruited;
        this.isFinished = isFinished;
        this.name = name;
        this.githubLink = githubLink;
        this.notionLink = notionLink;
        this.projectExplain = projectExplain;
        this.frontendNum = frontendNum;
        this.backendNum = backendNum;
        this.devopsNum = devopsNum;
        this.fullStackNum = fullStackNum;
        this.dataEngineerNum = dataEngineerNum;
        this.recruitExplain = recruitExplain;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public void update(boolean isRecruited, boolean isFinished, String name, String githubLink,
                      String notionLink, String projectExplain, Integer frontendNum, Integer backendNum,
                      Integer devopsNum, Integer fullStackNum, Integer dataEngineerNum, String recruitExplain) {
        this.isRecruited = isRecruited;
        this.isFinished = isFinished;
        this.name = name;
        this.githubLink = githubLink;
        this.notionLink = notionLink;
        this.projectExplain = projectExplain;
        this.frontendNum = frontendNum;
        this.backendNum = backendNum;
        this.devopsNum = devopsNum;
        this.fullStackNum = fullStackNum;
        this.dataEngineerNum = dataEngineerNum;
        this.recruitExplain = recruitExplain;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseLikeCount() {
        this.likeCount--;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }
} 