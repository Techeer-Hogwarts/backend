package backend.techeerzip.domain.studyTeam.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_teams")
public class StudyTeam {

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

    @Column(name = "study_explain", nullable = false, length = 3000)
    private String studyExplain;

    @Column(nullable = false, length = 3000)
    private String goal;

    @Column(nullable = false, length = 3000)
    private String rule;

    @Column(name = "recruit_num", nullable = false)
    private Integer recruitNum;

    @Column(name = "recruit_explain", nullable = false, length = 3000)
    private String recruitExplain;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "studyTeam", cascade = CascadeType.ALL)
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "studyTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyResultImage> studyResultImages = new ArrayList<>();

    @Builder
    public StudyTeam(
            boolean isRecruited,
            boolean isFinished,
            String name,
            String githubLink,
            String notionLink,
            String studyExplain,
            String goal,
            String rule,
            Integer recruitNum,
            String recruitExplain) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isRecruited = isRecruited;
        this.isFinished = isFinished;
        this.name = name;
        this.githubLink = githubLink;
        this.notionLink = notionLink;
        this.studyExplain = studyExplain;
        this.goal = goal;
        this.rule = rule;
        this.recruitNum = recruitNum;
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
            String studyExplain,
            String goal,
            String rule,
            Integer recruitNum,
            String recruitExplain) {
        this.isRecruited = isRecruited;
        this.isFinished = isFinished;
        this.name = name;
        this.githubLink = githubLink;
        this.notionLink = notionLink;
        this.studyExplain = studyExplain;
        this.goal = goal;
        this.rule = rule;
        this.recruitNum = recruitNum;
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
