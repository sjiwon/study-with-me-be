package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.domain.service.MemberReviewInspector;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class WriteMemberReviewUseCase {
    private final MemberRepository memberRepository;
    private final MemberReviewInspector memberReviewInspector;
    private final MemberReviewRepository memberReviewRepository;

    public Long invoke(final WriteMemberReviewCommand command) {
        final Member reviewer = memberRepository.getById(command.reviewerId());
        final Member reviewee = memberRepository.getById(command.revieweeId());

        memberReviewInspector.checkReviewerHasEligibilityToReview(reviewer, reviewee);
        return memberReviewRepository.save(MemberReview.doReview(reviewer, reviewee, command.content())).getId();
    }
}
