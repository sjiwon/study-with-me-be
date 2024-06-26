package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.memberreview.application.usecase.command.UpdateMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMemberReviewUseCase {
    private final MemberReviewRepository memberReviewRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final UpdateMemberReviewCommand command) {
        final MemberReview memberReview = memberReviewRepository.getWrittenReview(command.reviewerId(), command.revieweeId());
        memberReview.updateReview(command.content());
    }
}
