package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttachmentUploader {
    private final FileUploader fileUploader;

    public List<UploadAttachment> uploadAttachments(final List<RawFileData> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file -> new UploadAttachment(file.fileName(), fileUploader.uploadFile(file)))
                .toList();
    }
}
