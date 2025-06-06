package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import backend.techeerzip.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TechBloggingTerm")
public class TechBloggingTerm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermPeriod period;

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL)
    private List<TechBloggingRound> rounds = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL)
    private List<TechBloggingTermParticipant> participants = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    public void addRound(TechBloggingRound round) {
        this.rounds.add(round);
    }

    public void removeRound(TechBloggingRound round) {
        this.rounds.remove(round);
    }

    public String getTermName() {
        return period.getTermName(year);
    }

    public boolean isActive() {
        return !isDeleted;
    }

    public void softDelete() {
        this.isDeleted = true;
        for (TechBloggingRound round : this.rounds) {
            round.softDelete();
        }
        for (TechBloggingTermParticipant participant : this.participants) {
            participant.softDelete();
        }
    }

    public boolean isFirstHalf() {
        return period == TermPeriod.FIRST_HALF;
    }

    public static TechBloggingTerm create(int year, TermPeriod period) {
        return TechBloggingTerm.builder()
                .year(year)
                .period(period)
                .isDeleted(false)
                .build();
    }

    @Deprecated
    public static TechBloggingTerm create(int year, boolean firstHalf) {
        return create(year, TermPeriod.from(firstHalf));
    }
}
