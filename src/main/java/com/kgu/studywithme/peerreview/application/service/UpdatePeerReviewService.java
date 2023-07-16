package com.kgu.studywithme.peerreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.peerreview.application.usecase.command.UpdatePeerReviewUseCase;
import com.kgu.studywithme.peerreview.domain.PeerReview;
import com.kgu.studywithme.peerreview.domain.PeerReviewRepository;
import com.kgu.studywithme.peerreview.exception.PeerReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdatePeerReviewService implements UpdatePeerReviewUseCase {
    private final PeerReviewRepository peerReviewRepository;

    @Override
    public void updatePeerReview(final Command command) {
        final PeerReview peerReview = getPeerReview(command.reviewerId(), command.revieweeId());
        validateReviewSameAsBefore(peerReview, command.content());

        peerReview.updateReview(command.content());
    }

    private PeerReview getPeerReview(
            final Long reviewerId,
            final Long revieweeId
    ) {
        return peerReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow(() -> StudyWithMeException.type(PeerReviewErrorCode.PEER_REVIEW_NOT_FOUND));
    }

    private void validateReviewSameAsBefore(
            final PeerReview peerReview,
            final String updateContent
    ) {
        if (peerReview.isReviewSame(updateContent)) {
            throw StudyWithMeException.type(PeerReviewErrorCode.CONTENT_SAME_AS_BEFORE);
        }
    }
}
