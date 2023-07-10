package com.kgu.studywithme.study.application;

import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.application.MemberFindService;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class StudyReviewService {
    private final ReviewRepository reviewRepository;
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final StudyValidator studyValidator;

    @StudyWithMeWritableTransactional
    public void write(
            final Long studyId,
            final Long memberId,
            final String content
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member member = memberFindService.findById(memberId);

        study.writeReview(member, content);
    }

    @StudyWithMeWritableTransactional
    public void remove(
            final Long reviewId,
            final Long memberId
    ) {
        validateReviewWriter(reviewId, memberId);
        reviewRepository.deleteById(reviewId);
    }

    @StudyWithMeWritableTransactional
    public void update(
            final Long reviewId,
            final Long memberId,
            final String content
    ) {
        validateReviewWriter(reviewId, memberId);

        final Review review = findById(reviewId);
        review.updateReview(content);
    }

    private void validateReviewWriter(
            final Long reviewId,
            final Long memberId
    ) {
        studyValidator.validateReviewWriter(reviewId, memberId);
    }

    private Review findById(final Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> StudyWithMeException.type(StudyErrorCode.REVIEW_NOT_FOUND));
    }
}
