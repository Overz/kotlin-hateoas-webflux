package org.example.hateoas.validations.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.hateoas.validations.validators.LimitedNumberValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LimitedNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitedNumber {

	String message() default "{application.validations.annotations.LimitedNumber.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	int min() default 0;
	int max() default 100;
}
