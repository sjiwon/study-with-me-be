package com.kgu.studywithme.file.presentation.dto.request;

import com.kgu.studywithme.file.utils.validator.FileExistsConstraint;
import com.kgu.studywithme.file.utils.validator.ImageExtensionConstraint;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(
        String type,

        @FileExistsConstraint
        @ImageExtensionConstraint
        MultipartFile file
) {
}
