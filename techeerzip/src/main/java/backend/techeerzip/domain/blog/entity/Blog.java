package backend.techeerzip.domain.blog.entity;

import backend.techeerzip.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blogs")
public class Blog {

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

    @Column(nullable = false, length = 2000)
    private String url;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 300)
    private String author;

    @Column(name = "author_image", length = 300)
    private String authorImage;

    @Column(nullable = false, length = 300)
    private String category;

    @Column(length = 2000)
    private String thumbnail;

    @ElementCollection
    @CollectionTable(name = "blog_tags", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "tag", length = 300)
    private List<String> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Builder
    public Blog(String title, String url, LocalDateTime date, String author, String authorImage,
                String category, String thumbnail, List<String> tags, User user) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.title = title;
        this.url = url;
        this.date = date;
        this.author = author;
        this.authorImage = authorImage;
        this.category = category;
        this.thumbnail = thumbnail;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.user = user;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public void update(String title, String url, LocalDateTime date, String author, String authorImage,
                      String category, String thumbnail, List<String> tags) {
        this.title = title;
        this.url = url;
        this.date = date;
        this.author = author;
        this.authorImage = authorImage;
        this.category = category;
        this.thumbnail = thumbnail;
        this.tags = tags != null ? tags : new ArrayList<>();
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