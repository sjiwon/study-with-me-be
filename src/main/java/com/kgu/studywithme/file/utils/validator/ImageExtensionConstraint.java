package com.kgu.studywithme.file.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageExtensionConstraintValidator.class)
public @interface ImageExtensionConstraint {
    String message() default "이미지는 jpg, jpeg, png, gif만 허용합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
