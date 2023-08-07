package com.kgu.studywithme.upload.presentation.dto.request;

import com.kgu.studywithme.upload.utils.validator.ImageExtensionConstraint;
import com.kgu.studywithme.upload.utils.validator.ImageUploadCategoryConstraint;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(
        @ImageUploadCategoryConstraint
        String type,

        @ImageExtensionConstraint
        MultipartFile file
) {
}
