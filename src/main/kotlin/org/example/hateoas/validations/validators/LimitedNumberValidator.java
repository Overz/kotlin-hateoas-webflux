package org.example.hateoas.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.hateoas.validations.annotations.LimitedNumber;

public class LimitedNumberValidator implements ConstraintValidator<LimitedNumber, Integer> {
	private int min;
	private int max;
	@Override
	public void initialize(LimitedNumber constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value != null && value >= min && value <= max;
	}
}
