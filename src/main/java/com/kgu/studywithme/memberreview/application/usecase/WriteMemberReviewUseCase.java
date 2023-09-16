package com.kgu.studywithme.memberreview.application.usecase;

import com.kgu.studywithme.memberreview.application.usecase.command.WriteMemberReviewCommand;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.memberreview.domain.service.MemberReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WriteMemberReviewUseCase {
    private final MemberReviewValidator memberReviewValidator;
    private final MemberReviewRepository memberReviewRepository;

    public Long invoke(final WriteMemberReviewCommand command) {
        memberReviewValidator.validateReviewEligibility(command.reviewerId(), command.revieweeId());
        return memberReviewRepository.save(command.toDomain()).getId();
    }
}
