package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateMemberReviewUseCase {
    private final MemberReviewRepository memberReviewRepository;

    public void invoke(final UpdateMemberReviewCommand command) {
        final MemberReview memberReview = getWrittenReview(command.reviewerId(), command.revieweeId());
        memberReview.updateReview(command.content());
    }

    private MemberReview getWrittenReview(final Long reviewerId, final Long revieweeId) {
        return memberReviewRepository.findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow(() -> StudyWithMeException.type(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND));
    }
}
