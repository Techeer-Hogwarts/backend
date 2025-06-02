package backend.techeerzip.domain.resume.entity;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Resume")
public class Resume extends BaseEntity {

    @Id
    @SequenceGenerator(
            name = "resume_id_seq_gen",
            sequenceName = "Resume_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resume_id_seq_gen")
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false)
    private boolean isMain;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 50)
    private String category;

    @Builder
    public Resume(User user, String title, String url, String position, String category, boolean isMain) {
        this.user = user;
        this.title = title;
        this.url = url;
        this.position = position;
        this.category = category;
        this.isMain = isMain;
        this.isDeleted = false;
        this.likeCount = 0;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String url, String position, String category) {
        this.title = title;
        this.url = url;
        this.position = position;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementLikeCount() {
        this.likeCount--;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
