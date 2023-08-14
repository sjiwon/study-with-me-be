package com.kgu.studywithme.favorite.application.usecase.command;

public interface StudyLikeMarkingUseCase {
    Long invoke(final Command command);

    record Command(
            Long studyId,
            Long memberId
    ) {
    }
}
