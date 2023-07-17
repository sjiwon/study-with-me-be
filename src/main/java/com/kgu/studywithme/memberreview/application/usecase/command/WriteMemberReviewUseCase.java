package com.kgu.studywithme.memberreview.application.usecase.command;

public interface WriteMemberReviewUseCase {
    Long writeMemberReview(Command command);

    record Command(
            Long reviewerId,
            Long revieweeId,
            String content
    ) {
    }
}
