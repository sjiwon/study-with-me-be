package com.kgu.studywithme.file.application.usecase;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.application.usecase.command.UploadImageCommand;
import com.kgu.studywithme.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UploadImageUseCase {
    private final FileUploader fileUploader;

    public String invoke(final UploadImageCommand command) {
        return fileUploader.uploadFile(command.file());
    }
}
