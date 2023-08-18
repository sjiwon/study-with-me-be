package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.application.adapter.StudyReadAdapter;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.usecase.command.LeaveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.LEAVE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class LeaveParticipationService implements LeaveParticipationUseCase {
    private final StudyReadAdapter studyReadAdapter;
    private final StudyParticipantRepository studyParticipantRepository;

    @Override
    public void invoke(final Command command) {
        final Study study = studyReadAdapter.getById(command.studyId());
        validateMemberIsHost(study, command.participantId());

        study.removeParticipant();
        studyParticipantRepository.updateParticipantStatus(command.studyId(), command.participantId(), LEAVE);
    }

    private void validateMemberIsHost(final Study study, final Long participantId) {
        if (study.isHost(participantId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.HOST_CANNOT_LEAVE_STUDY);
        }
    }
}
