package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.adapter.StudyReadAdapter;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipantReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.REJECT;

@Service
@RequiredArgsConstructor
public class RejectParticipationService implements RejectParticipationUseCase {
    private final ParticipantReadAdapter participantReadAdapter;
    private final StudyReadAdapter studyReadAdapter;
    private final StudyParticipantJpaRepository studyParticipantJpaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        final Member applier = participantReadAdapter.getApplier(command.studyId(), command.applierId());
        final Study study = studyReadAdapter.getById(command.studyId());
        validateStudyInProgress(study);

        studyParticipantJpaRepository.updateParticipantStatus(command.studyId(), command.applierId(), REJECT);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyRejectedEvent(
                            applier.getEmail().getValue(),
                            applier.getNickname().getValue(),
                            study.getName().getValue(),
                            command.reason()
                    )
            );
        }
    }

    private void validateStudyInProgress(final Study study) {
        if (study.isTerminated()) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_TERMINATED);
        }
    }
}
