package com.kgu.studywithme.studyreview.application.usecase.command;

import com.kgu.studywithme.studyreview.domain.model.StudyReview;

public interface WriteStudyReviewUseCase {
    Long invoke(final Command command);

    record Command(
            Long studyId,
            Long memberId,
            String content
    ) {
        public StudyReview toDomain() {
            return StudyReview.writeReview(studyId, memberId, content);
        }
    }
}
