package backend.techeerzip.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import backend.techeerzip.global.entity.StatusCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PermissionRequest")
public class PermissionRequest {

    @Id
    @SequenceGenerator(
            name = "pr_id_seq_gen",
            sequenceName = "PermissionRequest_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pr_id_seq_gen")
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column( nullable = false)
    private int requestedRoleId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "PermissionRequest_userId_fkey"))
    private User user;

    @Builder
    public PermissionRequest(Integer userId, Integer requestedRoleId) {
        this.userId = userId;
        this.requestedRoleId = requestedRoleId;
        this.status = StatusCategory.PENDING;
    }

    public void approve() {
        this.status = StatusCategory.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = StatusCategory.REJECT;
        this.updatedAt = LocalDateTime.now();
    }
}
