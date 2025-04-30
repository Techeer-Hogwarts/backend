package backend.techeerzip.domain.studyTeam.entity;

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

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "StudyTeam")
public class StudyTeam extends BaseEntity {

    @OneToMany(mappedBy = "studyTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "studyTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<StudyResultImage> studyResultImages = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isRecruited;

    @Column(nullable = false)
    private boolean isFinished;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String githubLink;

    @Column(nullable = false, length = 500)
    private String notionLink;

    @Column(nullable = false, length = 3000)
    private String studyExplain;

    @Column(nullable = false, length = 3000)
    private String goal;

    @Column(nullable = false, length = 3000)
    private String rule;

    @Column(nullable = false)
    private Integer recruitNum;

    @Column(nullable = false, length = 3000)
    private String recruitExplain;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer viewCount;

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
