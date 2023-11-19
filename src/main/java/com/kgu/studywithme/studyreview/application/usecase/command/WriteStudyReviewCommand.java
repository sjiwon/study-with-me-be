package com.kgu.studywithme.studyreview.application.usecase.command;

public record WriteStudyReviewCommand(
        Long studyId,
        Long memberId,
        String content
) {
}
