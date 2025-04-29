package backend.techeerzip.domain.resume.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "is_main", nullable = false)
    private boolean isMain;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 100)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    public Resume(
            String title, String url, boolean isMain, String category, String position, User user) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.title = title;
        this.url = url;
        this.isMain = isMain;
        this.category = category;
        this.position = position;
        this.user = user;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public void update(String title, String url, boolean isMain, String category, String position) {
        this.title = title;
        this.url = url;
        this.isMain = isMain;
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
