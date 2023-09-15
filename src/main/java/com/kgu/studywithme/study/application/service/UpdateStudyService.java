package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.application.adapter.StudyDuplicateCheckRepositoryAdapter;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.model.RecruitmentStatus.IN_PROGRESS;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyService implements UpdateStudyUseCase {
    private final StudyDuplicateCheckRepositoryAdapter studyDuplicateCheckRepositoryAdapter;
    private final StudyReader studyReader;

    @Override
    public void invoke(final Command command) {
        validateNameIsUnique(command.studyId(), command.name());

        final Study study = studyReader.getById(command.studyId());
        study.update(
                command.name(),
                command.description(),
                command.capacity(),
                command.category(),
                command.thumbnail(),
                command.type(),
                command.province(),
                command.city(),
                command.recruitmentStatus() ? IN_PROGRESS : COMPLETE,
                command.minimumAttendanceForGraduation(),
                command.hashtags()
        );
    }

    private void validateNameIsUnique(final Long studyId, final String name) {
        if (studyDuplicateCheckRepositoryAdapter.isNameUsedByOther(studyId, name)) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }
}
