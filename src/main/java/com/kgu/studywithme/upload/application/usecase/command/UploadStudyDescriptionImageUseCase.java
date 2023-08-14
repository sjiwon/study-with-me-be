package com.kgu.studywithme.upload.application.usecase.command;

import com.kgu.studywithme.file.domain.RawFileData;

public interface UploadStudyDescriptionImageUseCase {
    String uploadStudyDescriptionImage(final Command command);

    record Command(
            RawFileData file
    ) {
    }
}
