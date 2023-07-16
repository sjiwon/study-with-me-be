package com.kgu.studywithme.member.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import com.kgu.studywithme.peerreview.domain.PeerReview;
import com.kgu.studywithme.peerreview.domain.PeerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class MemberReviewService {
    private final PeerReviewRepository peerReviewRepository;

    @StudyWithMeWritableTransactional
    public void updateReview(
            final Long revieweeId,
            final Long reviewerId,
            final String content
    ) {
        PeerReview peerReview = peerReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow(() -> StudyWithMeException.type(MemberErrorCode.PEER_REVIEW_NOT_FOUND));
        peerReview.updateReview(content);
    }
}
