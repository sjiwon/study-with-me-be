package com.kgu.studywithme.study.application.usecase;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.annotation.UseCase;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyCommand;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.service.StudyResourceValidator;
import lombok.RequiredArgsConstructor;

import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.OFF;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.ON;

@UseCase
@RequiredArgsConstructor
public class UpdateStudyUseCase {
    private final StudyResourceValidator studyResourceValidator;
    private final StudyRepository studyRepository;

    @StudyWithMeWritableTransactional
    public void invoke(final UpdateStudyCommand command) {
        studyResourceValidator.validateInUpdate(command.studyId(), command.name());

        final Study study = studyRepository.getById(command.studyId());
        study.update(
                command.name(),
                command.description(),
                command.capacity(),
                command.category(),
                command.thumbnail(),
                command.type(),
                command.province(),
                command.city(),
                command.recruitmentStatus() ? ON : OFF,
                command.minimumAttendanceForGraduation(),
                command.hashtags()
        );
    }
}
