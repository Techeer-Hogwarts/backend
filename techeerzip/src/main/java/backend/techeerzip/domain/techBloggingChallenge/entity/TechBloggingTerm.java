package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.util.ArrayList;
import java.util.List;

import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingTermParticipantAlreadyExistsException;
import jakarta.persistence.*;

import backend.techeerzip.domain.user.entity.User;
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

    /**
     * 사용자가 이미 해당 챌린지 기간에 참여했는지 확인합니다.
     * 
     * @param user 확인할 사용자
     * @return 이미 참여한 경우 true, 그렇지 않으면 false
     */
    public boolean isUserAlreadyParticipated(User user) {
        return this.participants.stream()
                .anyMatch(participant -> !participant.isDeleted() &&
                        participant.getUser().getId().equals(user.getId()));
    }

    /**
     * 사용자를 챌린지 기간에 참여시킵니다.
     * 
     * @param user 참여시킬 사용자
     * @throws TechBloggingTermParticipantAlreadyExistsException 이미 참여한 사용자인 경우
     */
    public void addParticipant(User user) {
        if (isUserAlreadyParticipated(user)) {
            throw new TechBloggingTermParticipantAlreadyExistsException();
        }
        TechBloggingTermParticipant participant = TechBloggingTermParticipant.create(this, user);
        this.participants.add(participant);
    }

    public static TechBloggingTerm create(int year, TermPeriod period) {
        return TechBloggingTerm.builder().year(year).period(period).isDeleted(false).build();
    }

    @Deprecated
    public static TechBloggingTerm create(int year, boolean firstHalf) {
        return create(year, TermPeriod.from(firstHalf));
    }
}
