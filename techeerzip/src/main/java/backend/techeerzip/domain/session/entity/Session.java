package backend.techeerzip.domain.session.entity;

import java.time.LocalDateTime;

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

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Session")
@SQLDelete(sql = "UPDATE \"Session\" SET \"isDeleted\" = true WHERE id = ?")
@SQLRestriction("\"isDeleted\" = false")
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            nullable = false,
            foreignKey = @ForeignKey(name = "Session_userId_fkey"))
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false, length = 3000)
    private String thumbnail;

    @Column(length = 3000)
    private String videoUrl;

    @Column(length = 3000)
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
