package com.kgu.studywithme.file.utils.converter;

import com.kgu.studywithme.file.domain.model.FileExtension;
import com.kgu.studywithme.file.domain.model.FileUploadType;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ASSIGNMENT;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ATTACHMENT;

public class FileConverter {
    public static RawFileData convertImageFile(final MultipartFile file, final FileUploadType uploadType) {
        if (file == null || file.isEmpty()) {
            throw StudyWithMeException.type(FileErrorCode.FILE_IS_NOT_UPLOAD);
        }

        final String fileName = file.getOriginalFilename();

        try {
            return new RawFileData(
                    fileName,
                    file.getContentType(),
                    FileExtension.getExtensionFromFileName(fileName),
                    uploadType,
                    file.getInputStream()
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RawFileData convertAssignmentFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        final String fileName = file.getOriginalFilename();

        try {
            return new RawFileData(
                    fileName,
                    file.getContentType(),
                    FileExtension.getExtensionFromFileName(fileName),
                    STUDY_WEEKLY_ASSIGNMENT,
                    file.getInputStream()
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<RawFileData> convertAttachmentFiles(final List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return List.of();
        }

        return files.stream()
                .map(file -> {
                    final String fileName = file.getOriginalFilename();

                    try {
                        return new RawFileData(
                                fileName,
                                file.getContentType(),
                                FileExtension.getExtensionFromFileName(fileName),
                                STUDY_WEEKLY_ATTACHMENT,
                                file.getInputStream()
                        );
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }
}
