package com.kgu.studywithme.global.infrastructure.file;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.upload.exception.UploadErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
    // Image
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
    GIF(".gif"),

    // Non-Image
    TXT(".txt"),
    DOC(".doc"),
    DOCX(".docx"),
    HWP(".hwp"),
    HWPX(".hwpx"),
    PDF(".pdf"),
    PPT(".ppt"),
    PPTX(".pptx"),
    ;

    private final String value;

    public static FileExtension getExtensionFromFileName(final String fileName) {
        final String fileExtension = extractFileExtension(fileName);

        return Arrays.stream(values())
                .filter(extension -> extension.value.equals(fileExtension))
                .findFirst()
                .orElseThrow(() -> StudyWithMeException.type(UploadErrorCode.INVALID_FILE_EXTENSION));
    }

    public static boolean isValidExtension(final String fileName) {
        final String fileExtension = extractFileExtension(fileName);

        return Arrays.stream(values())
                .anyMatch(extension -> extension.value.equals(fileExtension));
    }

    public static boolean isValidImageExtension(final String fileName) {
        final String fileExtension = extractFileExtension(fileName);

        return Stream.of(JPG, JPEG, PNG, GIF)
                .anyMatch(extension -> extension.value.equals(fileExtension));
    }

    private static String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
