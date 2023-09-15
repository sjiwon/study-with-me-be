package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyparticipant.application.adapter.ParticipateMemberReadAdapter;
import com.kgu.studywithme.studyparticipant.application.usecase.command.RejectParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.REJECT;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class RejectParticipationService implements RejectParticipationUseCase {
    private final ParticipateMemberReadAdapter participateMemberReadAdapter;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void invoke(final Command command) {
        final Member applier = participateMemberReadAdapter.getApplier(command.studyId(), command.applierId());
        final Study study = studyRepository.getById(command.studyId());
        validateStudyInProgress(study);

        rejectApplier(study, applier);
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

    private void rejectApplier(final Study study, final Member applier) {
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), REJECT);
    }
}
