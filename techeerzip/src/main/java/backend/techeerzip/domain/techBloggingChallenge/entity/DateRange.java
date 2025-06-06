package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record DateRange(
        @Column(nullable = false) LocalDate startDate,

        @Column(nullable = false) LocalDate endDate) {
    public DateRange {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }
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