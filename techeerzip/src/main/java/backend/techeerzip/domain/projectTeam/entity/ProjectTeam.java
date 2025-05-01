package backend.techeerzip.domain.projectTeam.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ProjectTeam")
public class ProjectTeam extends BaseEntity {

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TeamStack> teamStacks = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectResultImage> resultImages = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectMainImage> mainImages = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isRecruited = true;

    @Column(nullable = false)
    private boolean isFinished = true;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String githubLink;

    @Column(nullable = false, length = 500)
    private String notionLink;

    @Column(nullable = false, length = 500)
    private String projectExplain;

    @Column(nullable = false)
    private Integer frontendNum;

    @Column(nullable = false)
    private Integer backendNum;

    @Column(nullable = false)
    private Integer devopsNum;

    @Column(nullable = false)
    private Integer fullStackNum;

    @Column(nullable = false)
    private Integer dataEngineerNum;

    @Column(nullable = false, length = 3000)
    private String recruitExplain;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer viewCount;

    @Builder
    public ProjectTeam(
            boolean isRecruited,
            boolean isFinished,
            String name,
            String githubLink,
            String notionLink,
            String projectExplain,
            Integer frontendNum,
            Integer backendNum,
            Integer devopsNum,
            Integer fullStackNum,
            Integer dataEngineerNum,
            String recruitExplain) {
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

    public void update(
            boolean isRecruited,
            boolean isFinished,
            String name,
            String githubLink,
            String notionLink,
            String projectExplain,
            Integer frontendNum,
            Integer backendNum,
            Integer devopsNum,
            Integer fullStackNum,
            Integer dataEngineerNum,
            String recruitExplain) {
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
