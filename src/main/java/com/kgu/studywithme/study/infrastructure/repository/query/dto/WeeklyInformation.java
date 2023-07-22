package com.kgu.studywithme.study.infrastructure.repository.query.dto;

import com.kgu.studywithme.member.domain.Nickname;
import com.kgu.studywithme.studyweekly.domain.Period;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyInformation {
    private final Long id;
    private final String title;
    private final String content;
    private final int week;
    private final Period period;
    private final boolean assignmentExists;
    private final boolean autoAttendance;
    private final StudyMember creator;
    private List<UploadAttachment> attachments;
    private List<WeeklySubmit> submits;

    public record WeeklySubmit(
            StudyMember participant,
            String submitType,
            String submitFileName,
            String submitLink
    ) {
        @QueryProjection
        public WeeklySubmit(
                final Long submitterId,
                final Nickname submitterNickname,
                final UploadAssignment assignment
        ) {
            this(
                    new StudyMember(submitterId, submitterNickname.getValue()),
                    assignment.getType().getDescription(),
                    assignment.getUploadFileName(),
                    assignment.getLink()
            );
        }
    }

    @QueryProjection
    public WeeklyInformation(
            final Long id,
            final String title,
            final String content,
            final int week,
            final Period period,
            final boolean assignmentExists,
            final boolean autoAttendance,
            final Long creatorId,
            final Nickname creatorNickname
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.week = week;
        this.period = period;
        this.assignmentExists = assignmentExists;
        this.autoAttendance = autoAttendance;
        this.creator = new StudyMember(creatorId, creatorNickname.getValue());
    }

    public void applyAttachments(final List<UploadAttachment> attachments) {
        this.attachments = attachments;
    }

    public void applySubmits(final List<WeeklySubmit> submits) {
        this.submits = submits;
    }
}
