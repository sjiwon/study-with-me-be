package com.kgu.studywithme.study.domain.repository.query.dto;

import com.kgu.studywithme.member.domain.model.Nickname;
import com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class WeeklyInformation {
    private Long id;
    private String title;
    private String content;
    private int week;
    private Period period;
    private boolean assignmentExists;
    private boolean autoAttendance;
    private StudyMember creator;
    private List<WeeklyAttachment> attachments;
    private List<WeeklySubmit> submits;

    public record WeeklyAttachment(
            Long weeklyId,
            String uploadFileName,
            String link
    ) {
        @QueryProjection
        public WeeklyAttachment {
        }
    }

    public record WeeklySubmit(
            StudyMember participant,
            Long weeklyId,
            AssignmentSubmitType submitType,
            String submitFileName,
            String submitLink
    ) {
        @QueryProjection
        public WeeklySubmit(
                final Long submitterId,
                final Nickname submitterNickname,
                final Long weeklyId,
                final UploadAssignment assignment
        ) {
            this(
                    new StudyMember(submitterId, submitterNickname.getValue()),
                    weeklyId,
                    assignment.getSubmitType(),
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

    public void applyAttachments(final List<WeeklyAttachment> attachments) {
        this.attachments = attachments;
    }

    public void applySubmits(final List<WeeklySubmit> submits) {
        this.submits = submits;
    }
}
