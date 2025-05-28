package backend.techeerzip.domain.userExperience.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private Long userId;

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

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Builder
    public UserExperience(
            Long userId,
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished,
            String description) {
        this.userId = userId;
        this.position = position;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.isFinished = isFinished;
        this.description = description;
    }

    public void update(
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished,
            String description) {
        this.position = position;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.isFinished = isFinished;
        this.updatedAt = LocalDateTime.now();
        this.description = description;
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
