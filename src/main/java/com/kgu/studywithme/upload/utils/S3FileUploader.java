package com.kgu.studywithme.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.kgu.studywithme.upload.infrastructure.aws.BucketMetadata.*;
import static com.kgu.studywithme.upload.utils.FileUploadType.*;

@Slf4j
@Component
public class S3FileUploader implements FileUploader {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3FileUploader(
            final AmazonS3 amazonS3,
            @Value("${cloud.ncp.storage.bucket}") final String bucket
    ) {
        this.amazonS3 = amazonS3;
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
        final String fileName = createFileNameByType(type, file.getOriginalFilename());

        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try (final InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (final IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage(), e);
            throw StudyWithMeException.type(UploadErrorCode.S3_UPLOAD_FAILURE);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileNameByType(
            final FileUploadType type,
            final String originalFileName
    ) {
        final String fileName = UUID.randomUUID() + extractFileExtension(originalFileName);

        return switch (type) {
            case DESCRIPTION -> String.format(STUDY_DESCRIPTIONS, fileName);
            case IMAGE -> String.format(WEEKLY_IMAGES, fileName);
            case ATTACHMENT -> String.format(WEEKLY_ATTACHMENTS, fileName);
            default -> String.format(WEEKLY_SUBMITS, fileName);
        };
    }

    private String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
