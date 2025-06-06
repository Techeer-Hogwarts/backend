package backend.techeerzip.domain.techBloggingChallenge.entity;

import java.time.LocalDate;
import java.time.YearMonth;

public enum TermPeriod {
    FIRST_HALF("상반기", 3, 7),
    SECOND_HALF("하반기", 9, 2);

    private final String displayName;
    private final int startMonth;
    private final int endMonth;

    TermPeriod(String displayName, int startMonth, int endMonth) {
        this.displayName = displayName;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDate getStartDate(int year) {
        return LocalDate.of(year, startMonth, 1);
    }

    public LocalDate getEndDate(int year) {
        if (this == SECOND_HALF) {
            // 하반기는 다음 해 2월까지
            return LocalDate.of(
                    year + 1, endMonth, YearMonth.of(year + 1, endMonth).lengthOfMonth());
        }
        // 상반기는 같은 해 7월까지
        return LocalDate.of(year, endMonth, YearMonth.of(year, endMonth).lengthOfMonth());
    }

    public String getTermName(int year) {
        return year + "년 " + displayName;
    }

    public static TermPeriod from(boolean firstHalf) {
        return firstHalf ? FIRST_HALF : SECOND_HALF;
    }
}
