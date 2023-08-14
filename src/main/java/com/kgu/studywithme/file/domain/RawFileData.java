package com.kgu.studywithme.file.domain;

import java.io.InputStream;

public record RawFileData(
        InputStream content,
        String contentType,
        String originalFileName
) {
}
