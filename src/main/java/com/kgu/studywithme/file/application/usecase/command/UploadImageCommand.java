package com.kgu.studywithme.file.application.usecase.command;

import com.kgu.studywithme.file.domain.model.RawFileData;

public record UploadImageCommand(
        RawFileData file
) {
}
