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
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
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
    private final List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectResultImage> resultImages = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectMainImage> mainImages = new ArrayList<>();

    @OneToMany(mappedBy = "projectTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamStack> teamStacks = new ArrayList<>();

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

    public void update(TeamData teamData, Boolean isRecruited) {
        this.name = teamData.getName();
        this.projectExplain = teamData.getProjectExplain();
        this.isRecruited = isRecruited;
        this.isFinished = teamData.getIsFinished();
        if (teamData.getIsRecruited() != null) {
            this.recruitExplain = teamData.getRecruitExplain();
        }
        if (teamData.getGithubLink() != null) {
            this.githubLink = teamData.getGithubLink();
        }
        if (teamData.getNotionLink() != null) {
            this.notionLink = teamData.getNotionLink();
        }
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

    public void updateTeamStacks(List<TeamStack> stacks) {
        this.teamStacks = stacks;
    }

    public void addTeamStacks(List<TeamStack> stacks) {
        this.teamStacks.addAll(stacks);
    }

    public void addProjectMembers(List<ProjectMember> members) {
        this.projectMembers.addAll(members);
    }

    public void addProjectMainImages(List<ProjectMainImage> images) {
        this.mainImages.addAll(images);
    }

    public void addProjectResultImages(List<ProjectResultImage> images) {
        this.resultImages.addAll(images);
    }

    public void softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.isRecruited = false;
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

    public boolean hasUserId(Long userId) {
        return this.projectMembers.stream().anyMatch(m -> m.getUser().getId().equals(userId));
    }

    public boolean isRecruited() {
        return this.isRecruited;
    }

    public boolean isRecruitPosition(TeamRole teamRole) {
        return teamRole.getCount(this) > 0;
    }
}
