package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded private DateRange dateRange;

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
        try {
            this.dateRange = DateRange.of(startDate, endDate);
        } catch (IllegalArgumentException e) {
            throw new TechBloggingRoundInvalidDateRangeException();
        }
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    // 호환성을 위한 getter 메서드들
    public LocalDate getStartDate() {
        return dateRange != null ? dateRange.getStartDate() : null;
    }

    public LocalDate getEndDate() {
        return dateRange != null ? dateRange.getEndDate() : null;
    }

    public boolean isActive() {
        return dateRange != null && dateRange.isActive();
    }

    public long getDurationDays() {
        return dateRange != null ? dateRange.getDurationDays() : 0;
    }

    public static TechBloggingRound create(
            LocalDate startDate, LocalDate endDate, int sequence, TechBloggingTerm term) {
        return TechBloggingRound.builder()
                .dateRange(DateRange.of(startDate, endDate))
                .sequence(sequence)
                .isDeleted(false)
                .term(term)
                .build();
    }

    // 연도/반기 정보는 term을 통해 접근
    public int getYear() {
        return term.getYear();
    }

    public boolean isFirstHalf() {
        return term.isFirstHalf();
    }

    public TermPeriod getPeriod() {
        return term.getPeriod();
    }

    // 회차 이름 생성 (예: "2024년 상반기 1회차")
    public String getRoundName() {
        return String.format("%s %d회차", term.getTermName(), sequence);
    }
}
