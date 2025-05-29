package backend.techeerzip.domain.techBloggingChallenge.entity;

import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TechBloggingTermParticipant")
public class TechBloggingTermParticipant extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termId", nullable = false)
    private TechBloggingTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static TechBloggingTermParticipant create(TechBloggingTerm term, User user) {
        return TechBloggingTermParticipant.builder().term(term).user(user).isDeleted(false).build();
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
