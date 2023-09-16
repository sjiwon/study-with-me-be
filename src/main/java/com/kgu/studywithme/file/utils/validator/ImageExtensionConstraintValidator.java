package com.kgu.studywithme.file.utils.validator;

import com.kgu.studywithme.file.domain.model.FileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageExtensionConstraintValidator implements ConstraintValidator<ImageExtensionConstraint, MultipartFile> {
    @Override
    public boolean isValid(final MultipartFile file, final ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        return isAllowedExtension(file);
    }

    private boolean isAllowedExtension(final MultipartFile file) {
        return FileExtension.isValidImageExtension(file.getOriginalFilename());
    }
}
