package com.kgu.studywithme.memberreview.application.adapter;

import com.kgu.studywithme.memberreview.domain.MemberReview;

import java.util.Optional;

public interface MemberReviewHandlingRepositoryAdapter {
    Optional<MemberReview> getWrittenReviewForReviewee(final Long reviewerId, final Long revieweeId);

    boolean alreadyReviewedForMember(final Long reviewerId, final Long memberId);
}
