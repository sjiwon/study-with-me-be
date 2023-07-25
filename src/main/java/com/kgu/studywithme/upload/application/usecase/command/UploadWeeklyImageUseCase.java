package com.kgu.studywithme.upload.application.usecase.command;

import org.springframework.web.multipart.MultipartFile;

public interface UploadWeeklyImageUseCase {
    String uploadWeeklyImage(final Command command);

    record Command(
            MultipartFile file
    ) {
    }
}
