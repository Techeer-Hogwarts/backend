package backend.techeerzip.domain.event.entity;

import java.time.LocalDateTime;

import backend.techeerzip.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Event")
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 2000)
    private String title;

    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Column(length = 200)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder
    public Event(
            String category,
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String url,
            User user) {
        this.isDeleted = false;
        this.category = category;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
        this.user = user;
    }

    public void update(
            String category,
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String url) {
        this.category = category;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}