package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyreview.application.usecase.command.DeleteStudyReviewCommand;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteStudyReviewUseCase {
    private final StudyReviewRepository studyReviewRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final DeleteStudyReviewCommand command) {
        final StudyReview review = studyReviewRepository.getByIdWithWriter(command.reviewId());
        final Member member = memberRepository.getById(command.memberId());

        validateMemberIsReviewWriter(review, member);
        studyRepository.decreaseReviewCount(review.getStudy().getId());
        studyReviewRepository.delete(review);
    }

    private void validateMemberIsReviewWriter(final StudyReview review, final Member member) {
        if (!review.isWriter(member)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_WRITER_CAN_DELETE);
        }
    }
}
