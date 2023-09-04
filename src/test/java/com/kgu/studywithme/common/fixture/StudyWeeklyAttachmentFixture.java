package com.kgu.studywithme.common.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyAttachmentFixture {
    TXT_FILE("hello.txt", "https://cloud-front-url/bucket/attachments/uuid.txt"),
    HWPX_FILE("hello.hwpx", "https://cloud-front-url/bucket/attachments/uuid.hwpx"),
    PDF_FILE("hello.pdf", "https://cloud-front-url/bucket/attachments/uuid.pdf"),
    IMG_FILE("hello.img", "https://cloud-front-url/bucket/attachments/uuid.png"),
    ;

    private final String uploadFileName;
    private final String link;
}
