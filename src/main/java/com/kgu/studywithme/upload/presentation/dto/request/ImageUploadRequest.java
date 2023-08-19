package com.kgu.studywithme.upload.presentation.dto.request;

import com.kgu.studywithme.file.utils.validator.FileExistsConstraint;
import com.kgu.studywithme.upload.utils.validator.ImageExtensionConstraint;
import com.kgu.studywithme.upload.utils.validator.ImageUploadCategoryConstraint;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(
        @ImageUploadCategoryConstraint
        String type,

        @FileExistsConstraint
        @ImageExtensionConstraint
        MultipartFile file
) {
}
