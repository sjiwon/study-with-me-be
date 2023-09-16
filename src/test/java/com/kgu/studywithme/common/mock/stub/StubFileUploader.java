package com.kgu.studywithme.common.mock.stub;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;

public class StubFileUploader implements FileUploader {
    @Override
    public String uploadFile(final RawFileData file) {
        return "S3/" + file.fileName();
    }
}
