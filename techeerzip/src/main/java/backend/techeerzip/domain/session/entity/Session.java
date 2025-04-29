package backend.techeerzip.domain.session.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "likeCount", nullable = false)
    private int likeCount;

    @Column(name = "viewCount", nullable = false)
    private int viewCount;

    @Column(nullable = false, length = 3000)
    private String thumbnail;

    @Column(name = "videoUrl", length = 3000)
    private String videoUrl;

    @Column(name = "fileUrl", length = 3000)
    private String fileUrl;

    @Column(nullable = false, length = 50)
    private String presenter;

    @Column(nullable = false, length = 50)
    private String date;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String position;

    public Session(
            String title,
            String thumbnail,
            String videoUrl,
            String fileUrl,
            String presenter,
            String date,
            String category,
            String position,
            User user) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.title = title;
        this.likeCount = 0;
        this.viewCount = 0;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.fileUrl = fileUrl;
        this.presenter = presenter;
        this.date = date;
        this.category = category;
        this.position = position;
        this.user = user;
    }

    public void update(
            String title,
            String thumbnail,
            String videoUrl,
            String fileUrl,
            String presenter,
            String date,
            String category,
            String position) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.fileUrl = fileUrl;
        this.presenter = presenter;
        this.date = date;
        this.category = category;
        this.position = position;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseLikeCount() {
        this.likeCount--;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }
}
