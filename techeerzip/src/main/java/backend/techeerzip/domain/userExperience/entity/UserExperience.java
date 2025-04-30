package backend.techeerzip.domain.userExperience.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "UserExperience",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "UserExperience_userId_position_companyName_startDate_key",
                        columnNames = {"userId", "position", "companyName", "startDate"}))
public class UserExperience {

    @Id
    @SequenceGenerator(
            name = "ue_id_seq_gen",
            sequenceName = "UserExperience_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ue_id_seq_gen")
    private Integer id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 200)
    private String companyName;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private boolean isFinished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "UserExperience_userId_fkey"))
    private User user;

    @Builder
    public UserExperience(
            Integer userId,
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished) {
        this.userId = userId;
        this.position = position;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.isFinished = isFinished;
    }

    public void update(
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished) {
        this.position = position;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.isFinished = isFinished;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
