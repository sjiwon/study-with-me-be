package com.kgu.studywithme.upload.utils;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.kgu.studywithme.upload.infrastructure.aws.BucketMetadata.STUDY_DESCRIPTIONS;
import static com.kgu.studywithme.upload.infrastructure.aws.BucketMetadata.WEEKLY_ATTACHMENTS;
import static com.kgu.studywithme.upload.infrastructure.aws.BucketMetadata.WEEKLY_IMAGES;
import static com.kgu.studywithme.upload.infrastructure.aws.BucketMetadata.WEEKLY_SUBMITS;
import static com.kgu.studywithme.upload.utils.FileUploadType.ATTACHMENT;
import static com.kgu.studywithme.upload.utils.FileUploadType.DESCRIPTION;
import static com.kgu.studywithme.upload.utils.FileUploadType.IMAGE;
import static com.kgu.studywithme.upload.utils.FileUploadType.SUBMIT;

@Slf4j
@Component
public class S3FileUploader implements FileUploader {
    private final S3Template s3Template;
    private final String bucket;

    public S3FileUploader(
            final S3Template s3Template,
            @Value("${spring.cloud.aws.s3.bucket}") final String bucket
    ) {
        this.s3Template = s3Template;
        this.bucket = bucket;
    }

    @Override
    public String uploadStudyDescriptionImage(final MultipartFile file) {
        validateFileExists(file);
        return uploadFile(DESCRIPTION, file);
    }

    @Override
    public String uploadWeeklyImage(final MultipartFile file) {
        validateFileExists(file);
        return uploadFile(IMAGE, file);
    }

    @Override
    public String uploadWeeklyAttachment(final MultipartFile file) {
        validateFileExists(file);
        return uploadFile(ATTACHMENT, file);
    }

    @Override
    public String uploadWeeklySubmit(final MultipartFile file) {
        validateFileExists(file);
        return uploadFile(SUBMIT, file);
    }

    private void validateFileExists(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw StudyWithMeException.type(UploadErrorCode.FILE_IS_EMPTY);
        }
    }

    private String uploadFile(
            final FileUploadType type,
            final MultipartFile file
    ) {
        try (final InputStream inputStream = file.getInputStream()) {
            final String uploadFileName = createFileNameByType(type, file.getOriginalFilename());

            final ObjectMetadata objectMetadata = ObjectMetadata.builder()
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            return s3Template.upload(
                    bucket,
                    uploadFileName,
                    inputStream,
                    objectMetadata
            ).getURL().toString();
        } catch (final IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage(), e);
            throw StudyWithMeException.type(UploadErrorCode.S3_UPLOAD_FAILURE);
        }
    }

    private String createFileNameByType(
            final FileUploadType uploadType,
            final String fileName
    ) {
        final String uploadFileName = UUID.randomUUID() + FileExtension.getExtensionFromFileName(fileName).getValue();

        return switch (uploadType) {
            case DESCRIPTION -> String.format(STUDY_DESCRIPTIONS, uploadFileName);
            case IMAGE -> String.format(WEEKLY_IMAGES, uploadFileName);
            case ATTACHMENT -> String.format(WEEKLY_ATTACHMENTS, uploadFileName);
            default -> String.format(WEEKLY_SUBMITS, uploadFileName);
        };
    }
}
