package com.tui.proof.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<TelephoneConstraint, String> {


    @Override
    public void initialize(TelephoneConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.matches("[0-9]+") && (s.length() > 8) && (s.length() < 14);
    }
}
