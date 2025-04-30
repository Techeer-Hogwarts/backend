package backend.techeerzip.domain.blog.entity;

import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Blog")
public class Blog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false, length = 2000)
    private String url;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 300)
    private String author;

    @Column(length = 300)
    private String authorImage;

    @Column(nullable = false, length = 300)
    private String category;

    @Column(length = 2000)
    private String thumbnail;

    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> tags = List.of();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            foreignKey = @ForeignKey(name = "Blog_userId_fkey"),
            nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer viewCount;

    @Builder
    public Blog(
            String title,
            String url,
            LocalDateTime date,
            String author,
            String authorImage,
            String category,
            String thumbnail,
            List<String> tags,
            User user) {
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

    public void update(
            String title,
            String url,
            LocalDateTime date,
            String author,
            String authorImage,
            String category,
            String thumbnail,
            List<String> tags) {
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
