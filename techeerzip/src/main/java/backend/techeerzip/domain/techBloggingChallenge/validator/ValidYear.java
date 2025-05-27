package backend.techeerzip.domain.techBloggingChallenge.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidYearValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidYear {
    String message() default "현재 연도 이상만 입력 가능합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
