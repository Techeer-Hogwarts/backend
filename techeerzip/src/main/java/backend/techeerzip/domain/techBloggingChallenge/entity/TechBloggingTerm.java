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

    @Column(nullable = false)
    private boolean firstHalf;

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
        return String.format("%d년 %s 챌린지", year, firstHalf ? "상반기" : "하반기");
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

    public static TechBloggingTerm create(int year, boolean firstHalf) {
        return TechBloggingTerm.builder().year(year).firstHalf(firstHalf).isDeleted(false).build();
    }
}
