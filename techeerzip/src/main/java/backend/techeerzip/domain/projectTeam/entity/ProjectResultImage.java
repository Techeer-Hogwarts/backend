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

import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ProjectResultImage")
public class ProjectResultImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false, length = 3000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "projectTeamId",
            foreignKey = @ForeignKey(name = "ProjectResultImage_projectTeamId_fkey"),
            nullable = false)
    private ProjectTeam projectTeam;

    public ProjectResultImage(String imageUrl, ProjectTeam projectTeam) {
        this.imageUrl = imageUrl;
        this.projectTeam = projectTeam;
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
