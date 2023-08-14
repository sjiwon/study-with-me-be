package com.kgu.studywithme.upload.application.service;

import com.kgu.studywithme.file.application.service.FileUploader;
import com.kgu.studywithme.upload.application.usecase.command.UploadStudyDescriptionImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadStudyDescriptionImageService implements UploadStudyDescriptionImageUseCase {
    private final FileUploader fileUploader;

    @Override
    public String uploadStudyDescriptionImage(final Command command) {
        return fileUploader.uploadStudyDescriptionImage(command.file());
    }
}
