package com.kgu.studywithme.memberreview.application.usecase.command;

public interface UpdateMemberReviewUseCase {
    void updateMemberReview(Command command);

    record Command(
            Long reviewerId,
            Long revieweeId,
            String content
    ) {
    }
}
