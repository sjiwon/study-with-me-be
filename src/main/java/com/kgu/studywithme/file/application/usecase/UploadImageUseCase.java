package com.kgu.studywithme.file.application.usecase;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.application.usecase.command.UploadImageCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadImageUseCase {
    private final FileUploader fileUploader;

    public String invoke(final UploadImageCommand command) {
        return fileUploader.uploadFile(command.file());
    }
}
