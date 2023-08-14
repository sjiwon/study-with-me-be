package com.kgu.studywithme.upload.application.usecase.command;

import com.kgu.studywithme.file.domain.RawFileData;

public interface UploadWeeklyImageUseCase {
    String uploadWeeklyImage(final Command command);

    record Command(
            RawFileData file
    ) {
    }
}
