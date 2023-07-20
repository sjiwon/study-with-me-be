package com.kgu.studywithme.fixture;

import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kgu.studywithme.fixture.PeriodFixture.*;
import static com.kgu.studywithme.fixture.StudyWeeklyAttachmentFixture.*;

@Getter
@RequiredArgsConstructor
public enum StudyWeeklyFixture {
    STUDY_WEEKLY_1(
            "Week 1", "지정된 기간까지 과제 제출해주세요.",
            1, WEEK_1, true, true,
            List.of(
                    UploadAttachment.of(IMG_FILE.getUploadFileName(), IMG_FILE.getLink()),
                    UploadAttachment.of(PDF_FILE.getUploadFileName(), PDF_FILE.getLink())
            )
    ),
    STUDY_WEEKLY_2(
            "Week 2", "지정된 기간까지 과제 제출해주세요.",
            2, WEEK_2, true, true,
            List.of(
                    UploadAttachment.of(IMG_FILE.getUploadFileName(), IMG_FILE.getLink()),
                    UploadAttachment.of(HWPX_FILE.getUploadFileName(), HWPX_FILE.getLink())
            )
    ),
    STUDY_WEEKLY_3(
            "Week 3", "지정된 기간까지 과제 제출해주세요.",
            3, WEEK_3, true, true,
            List.of(UploadAttachment.of(PDF_FILE.getUploadFileName(), PDF_FILE.getLink()))
    ),
    STUDY_WEEKLY_4(
            "Week 4", "지정된 기간까지 과제 제출해주세요.",
            4, WEEK_4, true, true,
            List.of(UploadAttachment.of(TXT_FILE.getUploadFileName(), TXT_FILE.getLink()))
    ),
    STUDY_WEEKLY_5(
            "Week 5", "지정된 시간까지 다들 줌에 접속해주세요.",
            5, WEEK_5, false, false,
            List.of()
    ),
    STUDY_WEEKLY_6(
            "Week 6", "지정된 시간까지 다들 줌에 접속해주세요.",
            6, WEEK_6, false, false,
            List.of()
    ),
    ;

    private final String title;
    private final String content;
    private final int week;
    private final PeriodFixture period;
    private final boolean assignmentExists;
    private final boolean autoAttendance;
    private final List<UploadAttachment> attachments;

    public StudyWeekly toWeekly(final Long studyId, final Long creatorId) {
        return StudyWeekly.createWeekly(
                studyId,
                creatorId,
                title,
                content,
                week,
                period.toPeriod(),
                attachments
        );
    }

    public StudyWeekly toWeeklyWithAssignment(final Long studyId, final Long creatorId) {
        return StudyWeekly.createWeeklyWithAssignment(
                studyId,
                creatorId,
                title,
                content,
                week,
                period.toPeriod(),
                autoAttendance,
                attachments
        );
    }
}
