package com.kgu.studywithme.studyreview.application.usecase.command;

public interface UpdateStudyReviewUseCase {
    void invoke(final Command command);

    record Command(
            Long reviewId,
            Long memberId,
            String content
    ) {
    }
}
