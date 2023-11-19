package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.study.application.usecase.command.TerminateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TerminateStudyUseCase {
    private final StudyRepository studyRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final TerminateStudyCommand command) {
        final Study study = studyRepository.getById(command.studyId());
        study.terminate();
    }
}
