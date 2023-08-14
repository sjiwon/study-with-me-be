package com.kgu.studywithme.upload.application.service;

import com.kgu.studywithme.global.infrastructure.file.FileUploader;
import com.kgu.studywithme.upload.application.usecase.command.UploadWeeklyImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadWeeklyImageService implements UploadWeeklyImageUseCase {
    private final FileUploader fileUploader;

    @Override
    public String uploadWeeklyImage(final Command command) {
        return fileUploader.uploadWeeklyImage(command.file());
    }
}
