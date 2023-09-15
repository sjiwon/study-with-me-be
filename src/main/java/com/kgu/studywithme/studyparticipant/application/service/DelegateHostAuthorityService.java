package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantVerificationRepositoryAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.DelegateHostAuthorityUseCase;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class DelegateHostAuthorityService implements DelegateHostAuthorityUseCase {
    private final StudyRepository studyRepository;
    private final ParticipantVerificationRepositoryAdapter participantVerificationRepositoryAdapter;

    @Override
    public void invoke(final Command command) {
        final Study study = studyRepository.getById(command.studyId());
        validateStudyInProgress(study);
        validateNewHostIsCurrentHost(study, command.newHostId());
        validateNewHostIsParticipant(command.studyId(), command.newHostId());

        study.delegateHostAuthority(command.newHostId());
    }

    private void validateStudyInProgress(final Study study) {
        if (study.isTerminated()) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_TERMINATED);
        }
    }

    private void validateNewHostIsCurrentHost(final Study study, final Long newHostId) {
        if (study.isHost(newHostId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.SELF_DELEGATING_NOT_ALLOWED);
        }
    }

    private void validateNewHostIsParticipant(final Long studyId, final Long newHostId) {
        if (!participantVerificationRepositoryAdapter.isParticipant(studyId, newHostId)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.NON_PARTICIPANT_CANNOT_BE_HOST);
        }
    }
}
