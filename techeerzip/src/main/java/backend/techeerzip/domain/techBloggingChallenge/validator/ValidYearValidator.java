package backend.techeerzip.domain.techBloggingChallenge.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidYearValidator implements ConstraintValidator<ValidYear, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int currentYear = java.time.Year.now().getValue();
        return value >= currentYear;
    }
}
