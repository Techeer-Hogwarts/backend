package backend.techeerzip.global.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
