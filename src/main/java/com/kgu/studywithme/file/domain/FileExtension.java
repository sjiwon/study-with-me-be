package com.kgu.studywithme.file.domain;

import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
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
                .orElseThrow(() -> StudyWithMeException.type(FileErrorCode.INVALID_FILE_EXTENSION));
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
