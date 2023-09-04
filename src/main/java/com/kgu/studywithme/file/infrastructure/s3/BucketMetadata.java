package com.kgu.studywithme.file.infrastructure.s3;

public interface BucketMetadata {
    /**
     * 스터디 생성 시 설명 내부 이미지 업로드 버킷 -> descriptions/{UUID}.{File Extension}
     */
    String STUDY_DESCRIPTION_IMAGE = "images/study-description/%s";

    /**
     * Weekly 글 내부 이미지 업로드 버킷 -> images/{UUID}.{File Extension}
     */
    String WEEKLY_CONTENT_IMAGE = "images/weekly-content/%s";

    /**
     * Weekly 글 첨부파일 업로드 버킷 -> attachments/{UUID}.{File Extension}
     */
    String WEEKLY_ATTACHMENTS = "attachments/%s";

    /**
     * Weekly 과제 제출 버킷 -> submits/{UUID}.{File Extension}
     */
    String WEEKLY_SUBMITS = "submits/%s";
}
