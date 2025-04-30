package backend.techeerzip.domain.user.entity;

import backend.techeerzip.global.entity.StatusCategory;
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
@Table(name = "permission_requests")
public class PermissionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "requested_role_id", nullable = false)
    private Long requestedRoleId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "\"PermissionRequest_userId_fkey\""))
    private User user;

    @Builder
    public PermissionRequest(Long userId, Long requestedRoleId) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
