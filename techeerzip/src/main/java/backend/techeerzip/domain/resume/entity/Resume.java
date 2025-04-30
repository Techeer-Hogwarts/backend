package backend.techeerzip.domain.resume.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Resume\"")
public class Resume {

    @Id
    @SequenceGenerator(
            name = "resume_id_seq_gen",
            sequenceName = "\"Resume_id_seq\"",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resume_id_seq_gen")
    private Integer id;

    @CreationTimestamp
    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updatedAt",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(
            name = "isDeleted",
            nullable = false
    )
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "\"Resume_userId_fkey\""
            )
    )
    private User user;

    @Column(
            name = "title",
            nullable = false,
            length = 1000
    )
    private String title;

    @Column(
            name = "url",
            nullable = false,
            length = 1000
    )
    private String url;

    @Column(
            name = "isMain",
            nullable = false
    )
    private boolean isMain;

    @Column(
            name = "likeCount",
            nullable = false
    )
    private int likeCount;

    @Column(
            name = "viewCount",
            nullable = false
    )
    private int viewCount;

    @Column(
            name = "position",
            nullable = false,
            length = 100
    )
    private String position;

    @Column(
            name = "category",
            nullable = false,
            length = 50
    )
    private String category;
    public Resume(User user, String title, String url, String position, String category) {
        this.user = user;
        this.title = title;
        this.url = url;
        this.position = position;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.isMain = false;
        this.likeCount = 0;
        this.viewCount = 0;
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
