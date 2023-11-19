package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteStudyReviewUseCase {
    private final StudyReviewRepository studyReviewRepository;
    private final MemberRepository memberRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyReviewCommand command) {
        final StudyReview review = studyReviewRepository.getById(command.reviewId());
        final Member member = memberRepository.getById(command.memberId());

        validateMemberIsReviewWriter(review, member);
        studyReviewRepository.delete(review);
    }

    private void validateMemberIsReviewWriter(final StudyReview review, final Member member) {
        if (!review.isWriter(member)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_WRITER_CAN_DELETE);
        }
    }
}
