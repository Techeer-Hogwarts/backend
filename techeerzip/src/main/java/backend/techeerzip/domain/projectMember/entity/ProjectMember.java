package backend.techeerzip.domain.projectMember.entity;

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

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.type.TeamRole;
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
        name = "ProjectMember",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "ProjectMember_projectTeamId_userId_key",
                    columnNames = {"projectTeamId", "userId"})
        })
public class ProjectMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isLeader;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole teamRole;

    @Column(nullable = false, length = 3000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectTeamId", updatable = false, nullable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", updatable = false, nullable = false)
    private User user;

    @Builder
    public ProjectMember(
            boolean isLeader,
            TeamRole teamRole,
            String summary,
            StatusCategory status,
            ProjectTeam projectTeam,
            User user) {
        this.isLeader = isLeader;
        this.teamRole = teamRole;
        this.summary = summary;
        this.status = status;
        this.projectTeam = projectTeam;
        this.user = user;
    }

    public void update(TeamRole teamRole, StatusCategory status, Boolean isLeader) {
        this.teamRole = teamRole;
        this.status = status;
        this.isLeader = isLeader;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void toInactive() {
        this.isDeleted = true;
    }

    public void toActive(TeamRole teamRole, Boolean isLeader) {
        this.teamRole = teamRole;
        this.isLeader = isLeader;
        this.status = StatusCategory.APPROVED;
        this.isDeleted = false;
    }

    public void toApplicant() {
        this.status = StatusCategory.PENDING;
    }

    public void toActive() {
        this.status = StatusCategory.APPROVED;
        this.isDeleted = false;
    }

    public void toReject() {
        this.status = StatusCategory.REJECT;
    }
}
