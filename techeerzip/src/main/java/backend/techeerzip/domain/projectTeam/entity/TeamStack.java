package backend.techeerzip.domain.projectTeam.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TeamStack")
public class TeamStack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private boolean isMain;

    @Column(nullable = false)
    private Long stackId;

    @Column(nullable = false)
    private Long projectTeamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "projectTeamId",
            foreignKey = @ForeignKey(name = "TeamStack_projectTeamId_fkey"),
            insertable = false,
            updatable = false)
    private ProjectTeam projectTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stackId",
            foreignKey = @ForeignKey(name = "TeamStack_stackId_fkey"),
            insertable = false,
            updatable = false)
    private Stack stack;

    @Builder
    public TeamStack(boolean isMain, Long stackId, Long projectTeamId) {
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
