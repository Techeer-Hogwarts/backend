package backend.techeerzip.domain.techBloggingChallenge.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import backend.techeerzip.domain.techBloggingChallenge.dto.request.CreateSingleRoundRequest;

public class ValidDateRangeValidator
        implements ConstraintValidator<ValidDateRange, CreateSingleRoundRequest> {
    @Override
    public boolean isValid(CreateSingleRoundRequest value, ConstraintValidatorContext context) {
        if (value == null) return true; // null은 NotNull에서 걸러짐
        if (value.getStartDate() == null || value.getEndDate() == null)
            return true; // 각각의 NotNull에서 걸러짐
        return value.getEndDate().isAfter(value.getStartDate());
    }
}
