package com.kgu.studywithme.studyreview.application.usecase.command;

import com.kgu.studywithme.studyreview.domain.model.StudyReview;

public record WriteStudyReviewCommand(
        Long studyId,
        Long memberId,
        String content
) {
    public StudyReview toDomain() {
        return StudyReview.writeReview(studyId, memberId, content);
    }
}
