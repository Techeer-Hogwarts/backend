package backend.techeerzip.domain.bookmark.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "bookmarks",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "content_id", "category"})
        })
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 50)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Bookmark(Long contentId, String category, User user) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.contentId = contentId;
        this.category = category;
        this.user = user;
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    // TODO: 필요한 필드 추가
}
