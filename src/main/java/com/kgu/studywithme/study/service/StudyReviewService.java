package com.kgu.studywithme.study.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.service.MemberFindService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.review.Review;
import com.kgu.studywithme.study.domain.review.ReviewRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyReviewService {
    private final ReviewRepository reviewRepository;
    private final StudyFindService studyFindService;
    private final MemberFindService memberFindService;
    private final StudyValidator studyValidator;

    @Transactional
    public void write(
            final Long studyId,
            final Long memberId,
            final String content
    ) {
        final Study study = studyFindService.findByIdWithParticipants(studyId);
        final Member member = memberFindService.findById(memberId);

        study.writeReview(member, content);
    }

    @Transactional
    public void remove(
            final Long reviewId,
            final Long memberId
    ) {
        validateReviewWriter(reviewId, memberId);
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
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
