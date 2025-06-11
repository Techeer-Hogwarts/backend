package backend.techeerzip.domain.studyMember.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.BaseEntity;
import backend.techeerzip.global.entity.StatusCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "StudyMember",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "StudyMember_studyTeamId_userId_key",
                    columnNames = {"studyTeamId", "userId"})
        })
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isLeader;

    @Column(nullable = false, length = 3000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyTeamId", updatable = false)
    private StudyTeam studyTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", updatable = false)
    private User user;

    @Builder
    public StudyMember(
            boolean isLeader,
            String summary,
            StatusCategory status,
            StudyTeam studyTeam,
            User user) {
        this.isLeader = isLeader;
        this.summary = summary;
        this.status = status;
        this.studyTeam = studyTeam;
        this.user = user;
    }

    public void update(String summary, StatusCategory status) {
        this.summary = summary;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void changeLeaderStatus(boolean isLeader) {
        this.isLeader = isLeader;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        if (!isDeleted()) {
            this.isDeleted = true;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void toActive(Boolean isLeader) {
        this.isLeader = isLeader;
        this.status = StatusCategory.APPROVED;
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void toActive() {
        this.status = StatusCategory.APPROVED;
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isApproved() {
        return this.status.equals(StatusCategory.APPROVED);
    }

    public void toApplicant() {
        this.status = StatusCategory.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    public void toReject() {
        this.status = StatusCategory.REJECT;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRejected() {
        return this.status.equals(StatusCategory.REJECT);
    }

    public boolean isPending() {
        return this.status.equals(StatusCategory.PENDING);
    }

    public boolean isActive() {
        return !this.isDeleted && this.status.equals(StatusCategory.APPROVED);
    }
}
