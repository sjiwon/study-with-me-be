package com.kgu.studywithme.favorite.application.usecase.command;

public interface StudyLikeMarkingUseCase {
    Long likeMarking(Command command);

    record Command(
            Long studyId,
            Long memberId
    ) {
    }
}
