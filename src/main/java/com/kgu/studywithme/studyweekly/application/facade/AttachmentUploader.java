package com.kgu.studywithme.studyweekly.application.facade;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttachmentUploader {
    private final FileUploader fileUploader;

    public List<UploadAttachment> uploadAttachments(final List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file -> new UploadAttachment(file.getOriginalFilename(), fileUploader.uploadWeeklyAttachment(file)))
                .toList();
    }
}
