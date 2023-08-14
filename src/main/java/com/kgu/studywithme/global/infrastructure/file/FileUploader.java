package com.kgu.studywithme.global.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    /**
     * 스터디 생성 시 설명 내부 이미지 업로드
     */
    String uploadStudyDescriptionImage(final MultipartFile file);

    /**
     * Weekly 글 내부 이미지 업로드
     */
    String uploadWeeklyImage(final MultipartFile file);

    /**
     * Weekly 글 첨부파일 업로드
     */
    String uploadWeeklyAttachment(final MultipartFile file);

    /**
     * Weekly 과제 업로드
     */
    String uploadWeeklySubmit(final MultipartFile file);
}
