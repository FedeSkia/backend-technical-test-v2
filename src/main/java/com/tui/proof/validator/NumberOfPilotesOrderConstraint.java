package com.tui.proof.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PilotesOrderValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberOfPilotesOrderConstraint {

    String message() default "Invalid pilotes number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
