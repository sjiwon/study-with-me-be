package com.kgu.studywithme.upload.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileExistsConstraintValidator implements ConstraintValidator<FileExistsConstraint, MultipartFile> {
    @Override
    public boolean isValid(
            final MultipartFile file,
            final ConstraintValidatorContext context
    ) {
        return file != null && !file.isEmpty();
    }
}
