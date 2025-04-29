package backend.techeerzip.domain.studyMember.entity;

import backend.techeerzip.domain.permissionRequest.entity.StatusCategory;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_members",
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_study_member",
               columnNames = {"study_team_id", "user_id"}
           )
       })
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(name = "is_leader", nullable = false)
    private boolean isLeader;

    @Column(name = "study_team_id", nullable = false)
    private Long studyTeamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 3000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_team_id", insertable = false, updatable = false)
    private StudyTeam studyTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Builder
    public StudyMember(boolean isLeader, Long studyTeamId, Long userId,
                      String summary, StatusCategory status) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isLeader = isLeader;
        this.studyTeamId = studyTeamId;
        this.userId = userId;
        this.summary = summary;
        this.status = status;
    }

    public void update(String summary, StatusCategory status) {
        this.summary = summary;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeLeaderStatus(boolean isLeader) {
        this.isLeader = isLeader;
        this.updatedAt = LocalDateTime.now();
    }
} 