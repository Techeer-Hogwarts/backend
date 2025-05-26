package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    // 상반기/하반기 구분
    @Column(nullable = false)
    private boolean firstHalf;

    // 회차 시작 날짜
    @Column(nullable = false)
    private LocalDate startDate;

    // 회차 종료 날짜
    @Column(nullable = false)
    private LocalDate endDate;

    // 회사순서 sequence
    @Column(nullable = false)
    private Integer sequence;

    // 기준 연도(1회차 연도, 하반기일 때 1회차 연도 기준으로 roundName 생성)
    @Column(nullable = false)
    private Integer baseYear;

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
            LocalDate startDate, LocalDate endDate, int sequence, boolean firstHalf, int baseYear) {
        return TechBloggingRound.builder()
                .startDate(startDate)
                .endDate(endDate)
                .sequence(sequence)
                .firstHalf(firstHalf)
                .isDeleted(false)
                .baseYear(baseYear)
                .build();
    }
}
