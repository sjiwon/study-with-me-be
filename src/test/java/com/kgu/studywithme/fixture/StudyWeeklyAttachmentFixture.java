package com.kgu.studywithme.fixture;

import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyAttachmentFixture {
    PDF_FILE("hello.pdf", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.pdf"),
    TXT_FILE("hello.txt", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.txt"),
    HWP_FILE("hello.hwp", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.hwp"),
    IMG_FILE("hello.img", "https://kr.object.ncloudstorage.com/bucket/attachments/uuid.png"),
    ;

    private final String uploadFileName;
    private final String link;

    public StudyWeeklyAttachment toAttachment(final StudyWeekly studyWeekly) {
        return StudyWeeklyAttachment.addAttachmentFile(
                studyWeekly,
                UploadAttachment.of(uploadFileName, link)
        );
    }
}
