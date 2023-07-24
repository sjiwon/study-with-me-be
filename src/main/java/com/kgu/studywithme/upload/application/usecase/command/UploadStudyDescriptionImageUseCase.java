package com.kgu.studywithme.upload.application.usecase.command;

import org.springframework.web.multipart.MultipartFile;

public interface UploadStudyDescriptionImageUseCase {
    String uploadStudyDescriptionImage(final Command command);

    record Command(
            MultipartFile file
    ) {
    }
}
