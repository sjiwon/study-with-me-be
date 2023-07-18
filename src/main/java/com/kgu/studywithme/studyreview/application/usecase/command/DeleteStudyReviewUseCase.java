package com.kgu.studywithme.studyreview.application.usecase.command;

public interface DeleteStudyReviewUseCase {
    void deleteStudyReview(final Command command);

    record Command(
            Long reviewId,
            Long memberId
    ) {
    }
}