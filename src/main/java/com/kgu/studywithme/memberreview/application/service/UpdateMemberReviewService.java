package com.kgu.studywithme.memberreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewUseCase;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateMemberReviewService implements UpdateMemberReviewUseCase {
    private final MemberReviewRepository memberReviewRepository;

    @Override
    public void invoke(final Command command) {
        final MemberReview memberReview = getPeerReview(command.reviewerId(), command.revieweeId());
        memberReview.updateReview(command.content());
    }

    private MemberReview getPeerReview(
            final Long reviewerId,
            final Long revieweeId
    ) {
        return memberReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow(() -> StudyWithMeException.type(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND));
    }
}
