package backend.techeerzip.domain.studyTeam.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.studyTeam.dto.request.StudyData;
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
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isRecruited;

    @Column(nullable = false)
    private Boolean isFinished;

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

    public boolean isSameName(String name) {
        return this.name != null && this.name.equals(name);
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

    public void addMembers(List<StudyMember> incomingMembers) {
        this.studyMembers.addAll(incomingMembers);
    }

    public void update(StudyData studyData, boolean isRecruited) {
        this.name = studyData.getName();
        this.studyExplain = studyData.getStudyExplain();
        this.recruitExplain = studyData.getRecruitExplain();
        this.isRecruited = isRecruited;
        this.isFinished = studyData.getIsFinished();
        this.goal = studyData.getGoal();
        this.rule = studyData.getRule();
        this.githubLink = studyData.getGithubLink();
        this.notionLink = studyData.getNotionLink();
        this.recruitNum = studyData.getRecruitNum();
        this.updatedAt = LocalDateTime.now();
    }

    public List<LeaderInfo> getLeaders() {
        List<LeaderInfo> leaders = new ArrayList<>();
        for (StudyMember m : this.studyMembers) {
            if (m.isLeader()) {
                leaders.add(new LeaderInfo(m.getUser().getName(), m.getUser().getEmail()));
            }
        }
        return leaders;
    }

    public void addResultImage(List<StudyResultImage> images) {
        studyResultImages.addAll(images);
    }

    public void deleteResultImages(Set<Long> deleteResultImageIds) {
        if (deleteResultImageIds.isEmpty()) return;
        studyResultImages.removeIf(image -> deleteResultImageIds.contains(image.getId()));
    }

    public void softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.isRecruited = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRecruited() {
        return isRecruited || recruitNum > 0;
    }

    public void remove(StudyMember sm) {
        this.studyMembers.remove(sm);
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.isDeleted);
    }

    public List<StudyMember> getActiveMember() {
        return this.studyMembers.stream().filter(StudyMember::isActive).toList();
    }
}
