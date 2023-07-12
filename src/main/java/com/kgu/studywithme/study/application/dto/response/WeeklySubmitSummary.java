package com.kgu.studywithme.study.application.dto.response;

import com.kgu.studywithme.study.domain.week.submit.Submit;

public record WeeklySubmitSummary(
        StudyMember participant,
        String submitType,
        String submitFileName,
        String submitLink
) {
    public WeeklySubmitSummary(final Submit submit) {
        this(
                new StudyMember(submit.getParticipant()),
                submit.getUploadAssignment().getType().getDescription(),
                submit.getUploadAssignment().getUploadFileName(),
                submit.getUploadAssignment().getLink()
        );
    }
}