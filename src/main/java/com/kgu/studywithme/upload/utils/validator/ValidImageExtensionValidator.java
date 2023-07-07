package com.kgu.studywithme.upload.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidImageExtensionValidator implements ConstraintValidator<ValidImageExtension, MultipartFile> {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
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

    private boolean isNotAllowedExtension(MultipartFile file) {
        String extension = getExtention(file);
        return !ALLOWED_EXTENSIONS.contains(extension);
    }

    private String getExtention(MultipartFile file) {
        String uploadName = file.getOriginalFilename();
        return uploadName.substring(uploadName.lastIndexOf(".") + 1);
    }
}
