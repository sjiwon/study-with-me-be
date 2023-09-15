package com.kgu.studywithme.common.mock.stub;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import org.springframework.web.multipart.MultipartFile;

public class StubFileUploader implements FileUploader {
    @Override
    public String uploadStudyDescriptionImage(final MultipartFile file) {
        return "https://study-description-image";
    }

    @Override
    public String uploadWeeklyImage(final MultipartFile file) {
        return "https://weekly-image";
    }

    @Override
    public String uploadWeeklyAttachment(final MultipartFile file) {
        return "https://weekly-attachment";
    }

    @Override
    public String uploadWeeklySubmit(final MultipartFile file) {
        return "https://weekly-submit";
    }
}
