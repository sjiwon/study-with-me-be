package com.kgu.studywithme.file.domain.model;

import java.io.InputStream;

public record RawFileData(
        String fileName,
        String contenType,
        FileExtension extension,
        FileUploadType uploadType,
        InputStream content
) {
}
