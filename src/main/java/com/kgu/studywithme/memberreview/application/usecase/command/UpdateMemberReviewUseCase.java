package com.kgu.studywithme.memberreview.application.usecase.command;

public interface UpdateMemberReviewUseCase {
    void updateMemberReview(final Command command);

    record Command(
            Long reviewerId,
            Long revieweeId,
            String content
    ) {
    }
}
