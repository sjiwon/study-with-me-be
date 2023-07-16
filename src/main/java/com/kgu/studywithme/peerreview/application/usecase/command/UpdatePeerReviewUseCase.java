package com.kgu.studywithme.peerreview.application.usecase.command;

public interface UpdatePeerReviewUseCase {
    void updatePeerReview(Command command);

    record Command(
            Long reviewerId,
            Long revieweeId,
            String content
    ) {
    }
}
