package com.kgu.studywithme.upload.application.service;

import com.kgu.studywithme.upload.application.usecase.command.UploadStudyDescriptionImageUseCase;
import com.kgu.studywithme.upload.utils.FileUploader;
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
