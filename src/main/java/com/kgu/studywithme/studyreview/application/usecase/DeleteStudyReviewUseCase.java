package com.kgu.studywithme.studyreview.application.usecase;

import com.kgu.studywithme.global.exception.StudyWithMeException;
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

    public void invoke(final DeleteStudyReviewCommand command) {
        final StudyReview review = studyReviewRepository.getById(command.reviewId());
        validateMemberIsReviewWriter(review, command.memberId());

        studyReviewRepository.delete(review);
    }

    private void validateMemberIsReviewWriter(final StudyReview review, final Long memberId) {
        if (!review.isWriter(memberId)) {
            throw StudyWithMeException.type(StudyReviewErrorCode.ONLY_WRITER_CAN_DELETE);
        }
    }
}
