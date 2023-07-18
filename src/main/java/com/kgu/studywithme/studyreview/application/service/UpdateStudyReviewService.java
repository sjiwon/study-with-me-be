package com.kgu.studywithme.studyreview.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyreview.application.usecase.command.UpdateStudyReviewUseCase;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyReviewService implements UpdateStudyReviewUseCase {
    private final StudyReviewRepository studyReviewRepository;

    @Override
    public void updateStudyReview(final Command command) {
        final StudyReview review = findById(command.reviewId());
        validateMemberIsReviewWriter(review, command.memberId());

        review.updateReview(command.content());
    }

    private StudyReview findById(final Long reviewId) {
        return studyReviewRepository.findById(reviewId)
                .orElseThrow(() -> StudyWithMeException.type(StudyReviewErrorCode.STUDY_REVIEW_NOT_FOUND));
    }

    private void validateMemberIsReviewWriter(final StudyReview review, final Long memberId) {
        if (!review.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_WRITER_CAN_UPDATE);
        }
    }
}