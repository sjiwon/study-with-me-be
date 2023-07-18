package com.kgu.studywithme.studyreview.application.usecase.command;

public interface UpdateStudyReviewUseCase {
    void updateStudyReview(final Command command);

    record Command(
            Long reviewId,
            Long memberId,
            String content
    ) {
    }
}
