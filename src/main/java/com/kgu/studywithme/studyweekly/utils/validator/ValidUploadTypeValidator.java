package com.kgu.studywithme.studyweekly.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidUploadTypeValidator implements ConstraintValidator<ValidUploadType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("link", "file");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
