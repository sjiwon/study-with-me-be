package com.kgu.studywithme.studyreview.application.usecase.command;

public record DeleteStudyReviewCommand(
        Long reviewId,
        Long memberId
) {
}
