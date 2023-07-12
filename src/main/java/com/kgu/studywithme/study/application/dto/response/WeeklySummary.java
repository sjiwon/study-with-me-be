package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.study.domain.week.Period;
import com.kgu.studywithme.study.domain.week.Week;
import com.kgu.studywithme.study.domain.week.attachment.Attachment;
import com.kgu.studywithme.study.domain.week.attachment.UploadAttachment;
import com.kgu.studywithme.study.domain.week.submit.Submit;

import java.util.List;

public record WeeklySummary(
        Long id,
        String title,
        String content,
        int week,
        Period period,
        StudyMember creator,
        boolean assignmentExists,
        boolean autoAttendance,
        List<UploadAttachment> attachments,
        List<WeeklySubmitSummary> submits
) {
    public WeeklySummary(final Week week) {
        this(
                week.getId(),
                week.getTitle(),
                week.getContent(),
                week.getWeek(),
                week.getPeriod(),
                new StudyMember(week.getCreator()),
                week.isAssignmentExists(),
                week.isAutoAttendance(),
                transformAttachments(week.getAttachments()),
                transformSubmits(week.getSubmits())
        );
    }

    private static List<UploadAttachment> transformAttachments(final List<Attachment> attachments) {
        return attachments.stream()
                .map(Attachment::getUploadAttachment)
                .toList();
    }

    private static List<WeeklySubmitSummary> transformSubmits(final List<Submit> submits) {
        return submits.stream()
                .map(WeeklySubmitSummary::new)
                .toList();
    }
}