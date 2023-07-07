package com.kgu.studywithme.upload.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidImageUploadTypeValidator implements ConstraintValidator<ValidImageUploadType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("weekly", "description");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
