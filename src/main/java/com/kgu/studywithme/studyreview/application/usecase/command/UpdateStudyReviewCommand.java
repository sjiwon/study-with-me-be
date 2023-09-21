package com.kgu.studywithme.studyreview.application.usecase.command;

public record UpdateStudyReviewCommand(
        Long reviewId,
        Long memberId,
        String content
) {
}
