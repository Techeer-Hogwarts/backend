package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TechBloggingTermParticipant")
public class TechBloggingTermParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termId", nullable = false)
    private TechBloggingTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    public static TechBloggingTermParticipant create(TechBloggingTerm term, User user) {
        return TechBloggingTermParticipant.builder()
                .term(term)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
