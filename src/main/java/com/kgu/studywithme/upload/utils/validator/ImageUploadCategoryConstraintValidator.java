package com.kgu.studywithme.upload.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ImageUploadCategoryConstraintValidator implements ConstraintValidator<ImageUploadCategoryConstraint, String> {
    private static final List<String> ALLOWED_TYPE = List.of("weekly", "description");

    @Override
    public boolean isValid(
            final String value,
            final ConstraintValidatorContext context
    ) {
        return ALLOWED_TYPE.contains(value);
    }
}
