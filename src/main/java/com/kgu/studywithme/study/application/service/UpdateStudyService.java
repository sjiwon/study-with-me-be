package com.kgu.studywithme.study.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.application.usecase.command.UpdateStudyUseCase;
import com.kgu.studywithme.study.domain.Capacity;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyName;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.study.domain.RecruitmentStatus.COMPLETE;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class UpdateStudyService implements UpdateStudyUseCase {
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final QueryStudyByIdService queryStudyByIdService;

    @Override
    public void updateStudy(final Command command) {
        validateNameIsUnique(command.studyId(), command.name());
        validateCapacityCanCoverCurrentParticipants(command.studyId(), command.capacity());

        final Study study = queryStudyByIdService.findById(command.studyId());
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

    private void validateNameIsUnique(final Long studyId, final StudyName name) {
        if (studyRepository.isNameUsedByOther(studyId, name.getValue())) {
            throw StudyWithMeException.type(StudyErrorCode.DUPLICATE_NAME);
        }
    }

    private void validateCapacityCanCoverCurrentParticipants(
            final Long studyId,
            final Capacity capacity
    ) {
        final int currentParticipants = studyParticipantRepository.getCurrentParticipantsCount(studyId);

        if (capacity.isLessThan(currentParticipants)) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS);
        }
    }
}
