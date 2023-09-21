package com.kgu.studywithme.file.application.adapter;

import com.kgu.studywithme.file.domain.model.RawFileData;

public interface FileUploader {
    String uploadFile(final RawFileData file);
}
