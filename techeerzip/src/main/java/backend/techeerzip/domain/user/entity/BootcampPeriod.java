package backend.techeerzip.domain.user.entity;

import java.time.LocalDate;

public enum BootcampPeriod {
    FIRST_HALF("상반기 부트캠프", 6, 8),
    SECOND_HALF("하반기 부트캠프 ", 12, 2);

    private final String displayName;
    private final int startMonth;
    private final int endMonth;

    private static final int BASE_YEAR = 2025;
    private static final BootcampPeriod BASE_TERM = FIRST_HALF;
    private static final int BASE_GENERATION = 10;

    BootcampPeriod(String displayName, int startMonth, int endMonth) {
        this.displayName = displayName;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Integer calculateGeneration(LocalDate joinDate) {
        int year = joinDate.getYear();
        int month = joinDate.getMonthValue();

        BootcampPeriod currentPeriod;
        if (month >= 6 && month <= 8) {
            currentPeriod = FIRST_HALF;
        } else if (month == 12 || month == 1 || month == 2) {
            currentPeriod = SECOND_HALF;
            if (month == 1 || month == 2) {
                year -= 1;
            }
        } else {
            return null;
        }

        int termDiff =
                ((year - BASE_YEAR) * 2)
                        + (currentPeriod == SECOND_HALF ? 1 : 0)
                        - (BASE_TERM == SECOND_HALF ? 1 : 0);

        return BASE_GENERATION + termDiff;
    }
}
