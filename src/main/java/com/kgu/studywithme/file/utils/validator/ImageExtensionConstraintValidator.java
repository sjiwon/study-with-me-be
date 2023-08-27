package com.kgu.studywithme.file.utils.validator;

import com.kgu.studywithme.file.domain.FileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageExtensionConstraintValidator implements ConstraintValidator<ImageExtensionConstraint, MultipartFile> {
    @Override
    public boolean isValid(
            final MultipartFile file,
            final ConstraintValidatorContext context
    ) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        if (isNotAllowedExtension(file)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("이미지는 jpg, jpeg, png, gif만 허용합니다.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isNotAllowedExtension(final MultipartFile file) {
        return !FileExtension.isValidImageExtension(file.getOriginalFilename());
    }
}
