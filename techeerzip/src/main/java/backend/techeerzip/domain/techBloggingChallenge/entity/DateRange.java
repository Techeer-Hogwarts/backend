package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateRange {
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isWithin(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return isWithin(today);
    }

    public long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
    }

    public boolean overlaps(DateRange other) {
        return other != null &&
                !this.endDate.isBefore(other.startDate) &&
                !other.endDate.isBefore(this.startDate);
    }

    public static DateRange of(LocalDate startDate, LocalDate endDate) {
        return new DateRange(startDate, endDate);
    }
}