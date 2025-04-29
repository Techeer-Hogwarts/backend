package backend.techeerzip.domain.stack.entity;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_stacks")
public class TeamStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(name = "is_main", nullable = false)
    private boolean isMain;

    @Column(name = "stack_id", nullable = false)
    private Long stackId;

    @Column(name = "project_team_id", nullable = false)
    private Long projectTeamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_team_id", insertable = false, updatable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id", insertable = false, updatable = false)
    private Stack stack;

    @Builder
    public TeamStack(boolean isMain, Long stackId, Long projectTeamId) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isMain = isMain;
        this.stackId = stackId;
        this.projectTeamId = projectTeamId;
    }

    public void update(boolean isMain) {
        this.isMain = isMain;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
} 