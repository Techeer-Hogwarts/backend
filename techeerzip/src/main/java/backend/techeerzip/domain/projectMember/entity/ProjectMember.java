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

import backend.techeerzip.domain.permissionRequest.entity.StatusCategory;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.BaseEntity;
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
                    name = "uk_project_member",
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

    @Column(nullable = false, length = 100)
    private String teamRole;

    @Column(nullable = false)
    private Long projectTeamId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 3000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectTeamId", insertable = false, updatable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Builder
    public ProjectMember(
            boolean isLeader,
            String teamRole,
            Long projectTeamId,
            Long userId,
            String summary,
            StatusCategory status) {
        this.isLeader = isLeader;
        this.teamRole = teamRole;
        this.projectTeamId = projectTeamId;
        this.userId = userId;
        this.summary = summary;
        this.status = status;
    }

    public void update(String teamRole, String summary, StatusCategory status) {
        this.teamRole = teamRole;
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
