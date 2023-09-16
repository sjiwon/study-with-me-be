package com.kgu.studywithme.memberreview.application.usecase.command;

import com.kgu.studywithme.memberreview.domain.model.MemberReview;

public record WriteMemberReviewCommand(
        Long reviewerId,
        Long revieweeId,
        String content
) {
    public MemberReview toDomain() {
        return MemberReview.doReview(reviewerId, revieweeId, content);
    }
}
