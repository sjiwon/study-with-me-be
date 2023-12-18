package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class AttachmentUploader {
    private final FileUploader fileUploader;
    private final Executor executor;

    public AttachmentUploader(
            final FileUploader fileUploader,
            @Qualifier("fileUploadExecutor") final Executor executor
    ) {
        this.fileUploader = fileUploader;
        this.executor = executor;
    }

    public List<UploadAttachment> uploadAttachments(final List<RawFileData> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        final List<CompletableFuture<UploadAttachment>> result = files.stream()
                .map(file -> CompletableFuture.supplyAsync(
                        () -> new UploadAttachment(file.fileName(), fileUploader.uploadFile(file)),
                        executor
                ))
                .toList();

        return result.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
