package com.kgu.studywithme.favorite.application.usecase.command;

public interface StudyLikeCancellationUseCase {
    void likeCancellation(Command command);

    record Command(
            Long studyId,
            Long memberId
    ) {
    }
}
