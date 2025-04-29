package backend.techeerzip.domain.userExperience.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_experiences",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_experience",
                    columnNames = {"user_id", "position", "company_name", "start_date"})
        })
public class UserExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "is_finished", nullable = false)
    private boolean isFinished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Builder
    public UserExperience(
            Long userId,
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
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
