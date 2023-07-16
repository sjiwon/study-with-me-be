package com.kgu.studywithme.peerreview.application.usecase.command;

public interface WritePeerReviewUseCase {
    void writePeerReview(Command command);

    record Command(
            Long reviewerId,
            Long revieweeId,
            String content
    ) {
    }
}
