package com.tui.proof.validator;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PilotesOrderValidator implements ConstraintValidator<NumberOfPilotesOrderConstraint, Integer> {

    private final List<Integer> numberOfPilotes;

    public PilotesOrderValidator(@Value("${numberOfPilotes}") List<Integer> numberOfPilotes) {
        this.numberOfPilotes = numberOfPilotes;
    }

    @Override
    public void initialize(NumberOfPilotesOrderConstraint constraintAnnotation) { }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && (numberOfPilotes.contains(value));
    }
}
