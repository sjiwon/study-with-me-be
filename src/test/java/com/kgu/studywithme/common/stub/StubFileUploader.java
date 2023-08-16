package com.kgu.studywithme.common.stub;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.RawFileData;

public class StubFileUploader implements FileUploader {
    @Override
    public String uploadStudyDescriptionImage(final RawFileData file) {
        return "https://study-description-image";
    }

    @Override
    public String uploadWeeklyImage(final RawFileData file) {
        return "https://weekly-image";
    }

    @Override
    public String uploadWeeklyAttachment(final RawFileData file) {
        return "https://weekly-attachment";
    }

    @Override
    public String uploadWeeklySubmit(final RawFileData file) {
        return "https://weekly-submit";
    }
}
