package com.tui.proof.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PilotesOrderValidator implements ConstraintValidator<NumberOfPilotesOrderConstraint, Integer> {

    @Override
    public void initialize(NumberOfPilotesOrderConstraint constraintAnnotation) { }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && (value.equals(5) || value.equals(10) || value.equals(15));
    }
}
