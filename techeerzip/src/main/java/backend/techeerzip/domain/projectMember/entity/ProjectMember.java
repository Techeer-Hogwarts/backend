package backend.techeerzip.domain.projectMember.entity;

import backend.techeerzip.domain.permissionRequest.entity.StatusCategory;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
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
@Table(name = "project_members",
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_project_member",
               columnNames = {"project_team_id", "user_id"}
           )
       })
public class ProjectMember {

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

    @Column(name = "team_role", nullable = false, length = 100)
    private String teamRole;

    @Column(name = "project_team_id", nullable = false)
    private Long projectTeamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 3000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", insertable = false, updatable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Builder
    public ProjectMember(boolean isLeader, String teamRole, Long projectTeamId, Long userId,
                        String summary, StatusCategory status) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
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