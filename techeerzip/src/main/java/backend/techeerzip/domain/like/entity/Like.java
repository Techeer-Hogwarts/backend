package backend.techeerzip.domain.like.entity;

import backend.techeerzip.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String targetType; // BLOG, EVENT, etc.

    @Column(nullable = false)
    private String createdAt;

    // TODO: 필요한 필드 추가
} 