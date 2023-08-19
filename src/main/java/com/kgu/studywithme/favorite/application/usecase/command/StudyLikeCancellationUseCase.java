package com.kgu.studywithme.favorite.application.usecase.command;

public interface StudyLikeCancellationUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long memberId
    ) {
    }
}
