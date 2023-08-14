package com.kgu.studywithme.studyreview.application.usecase.command;

public interface WriteStudyReviewUseCase {
    Long invoke(final Command command);

    record Command(
            Long studyId,
            Long memberId,
            String content
    ) {
    }
}
