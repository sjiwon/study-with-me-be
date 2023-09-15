package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class LeaveParticipationService implements LeaveParticipationUseCase {
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public void invoke(final Command command) {
        final Study study = studyRepository.getById(command.studyId());
        validateMemberIsHost(study, command.participantId());
        leaveStudy(study, command.participantId());
    }

    private void validateMemberIsHost(final Study study, final Long participantId) {
        if (study.isHost(participantId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY);
        }
    }

    private void leaveStudy(final Study study, final Long participantId) {
        study.removeParticipant();
        studyParticipantRepository.updateParticipantStatus(study.getId(), participantId, LEAVE);
    }
}
