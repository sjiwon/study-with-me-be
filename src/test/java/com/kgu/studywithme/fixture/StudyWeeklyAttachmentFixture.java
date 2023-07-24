package com.kgu.studywithme.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyAttachmentFixture {
    TXT_FILE("hello.txt", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.txt"),
    HWPX_FILE("hello.hwpx", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.hwpx"),
    PDF_FILE("hello.pdf", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.pdf"),
    IMG_FILE("hello.img", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.png"),
    ;

    private final String uploadFileName;
    private final String link;
}
