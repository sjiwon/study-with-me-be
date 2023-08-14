package com.kgu.studywithme.file.application.service;

import com.kgu.studywithme.file.domain.RawFileData;

public interface FileUploader {
    /**
     * 스터디 생성 시 설명 내부 이미지 업로드
     */
    String uploadStudyDescriptionImage(final RawFileData file);

    /**
     * Weekly 글 내부 이미지 업로드
     */
    String uploadWeeklyImage(final RawFileData file);

    /**
     * Weekly 글 첨부파일 업로드
     */
    String uploadWeeklyAttachment(final RawFileData file);

    /**
     * Weekly 과제 업로드
     */
    String uploadWeeklySubmit(final RawFileData file);
}
