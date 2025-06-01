package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import backend.techeerzip.domain.techBloggingChallenge.exception.TechBloggingRoundInvalidDateRangeException;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TechBloggingRound")
public class TechBloggingRound extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    // 회차 시작 날짜
    @Column(nullable = false)
    private LocalDate startDate;

    // 회차 종료 날짜
    @Column(nullable = false)
    private LocalDate endDate;

    // 회차 순서
    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termId", nullable = false)
    private TechBloggingTerm term;

    @Builder.Default
    @OneToMany(mappedBy = "techBloggingRound")
    private List<TechBloggingAttendance> techBloggingAttendances = new ArrayList<>();

    public void updateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new TechBloggingRoundInvalidDateRangeException();
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public static TechBloggingRound create(
            LocalDate startDate, LocalDate endDate, int sequence, TechBloggingTerm term) {
        return TechBloggingRound.builder()
                .startDate(startDate)
                .endDate(endDate)
                .sequence(sequence)
                .isDeleted(false)
                .term(term)
                .build();
    }

    // 연도/반기 정보는 participation을 통해 접근
    public int getYear() {
        return term.getYear();
    }

    public boolean isFirstHalf() {
        return term.isFirstHalf();
    }

    // 회차 이름 생성 (예: "2024 상반기 1회차")
    public String getRoundName() {
        return String.format("%d %s %d회차", getYear(), isFirstHalf() ? "상반기" : "하반기", sequence);
    }
}
