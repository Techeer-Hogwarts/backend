package backend.techeerzip.domain.userExperience.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "\"UserExperience\"",
        uniqueConstraints = @UniqueConstraint(
                name = "\"UserExperience_userId_position_companyName_startDate_key\"",
                columnNames = {"userId", "position", "companyName", "startDate"}
        )
)
public class UserExperience {

    @Id
    @SequenceGenerator(
            name = "ue_id_seq_gen",
            sequenceName = "\"UserExperience_id_seq\"",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ue_id_seq_gen")
    private Integer id;

    @CreationTimestamp
    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updatedAt",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(
            name = "isDeleted",
            nullable = false
    )
    private boolean isDeleted;

    @Column(
            name = "userId",
            nullable = false
    )
    private Integer userId;

    @Column(
            name = "position",
            nullable = false,
            length = 100
    )
    private String position;

    @Column(
            name = "companyName",
            nullable = false,
            length = 200
    )
    private String companyName;

    @Column(
            name = "startDate",
            nullable = false
    )
    private LocalDateTime startDate;

    @Column(
            name = "endDate"
    )
    private LocalDateTime endDate;

    @Column(
            name = "category",
            nullable = false,
            length = 100
    )
    private String category;

    @Column(
            name = "isFinished",
            nullable = false
    )
    private boolean isFinished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                    name = "\"UserExperience_userId_fkey\""
            )
    )
    private User user;

    @Builder
    public UserExperience(
            Integer userId,
            String position,
            String companyName,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String category,
            boolean isFinished
    ) {
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
            boolean isFinished
    ) {
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
