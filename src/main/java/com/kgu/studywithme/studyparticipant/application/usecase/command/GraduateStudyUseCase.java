package com.kgu.studywithme.studyparticipant.application.usecase.command;

public interface GraduateStudyUseCase {
    void graduateStudy(final Command command);

    record Command(
            Long studyId,
            Long participantId
    ) {
    }
}
