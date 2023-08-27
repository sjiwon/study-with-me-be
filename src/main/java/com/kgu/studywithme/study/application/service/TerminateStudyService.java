package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyUseCase;
import com.kgu.studywithme.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class TerminateStudyService implements TerminateStudyUseCase {
    private final StudyReader studyReader;

    @Override
    public void invoke(final Command command) {
        final Study study = studyReader.getById(command.studyId());
        study.terminate();
    }
}
