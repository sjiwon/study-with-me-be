package com.kgu.studywithme.favorite.application.usecase.command;

public interface StudyLikeMarkingUseCase {
    Long likeMarking(final Command command);

    record Command(
            Long studyId,
            Long memberId
    ) {
    }
}
