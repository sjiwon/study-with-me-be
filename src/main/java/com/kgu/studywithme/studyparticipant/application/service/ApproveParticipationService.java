package com.kgu.studywithme.studyparticipant.application.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.application.QueryStudyByIdService;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.application.usecase.command.ApproveParticipationUseCase;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;

@Service
@StudyWithMeWritableTransactional
@RequiredArgsConstructor
public class ApproveParticipationService implements ApproveParticipationUseCase {
    private final QueryStudyByIdService queryStudyByIdService;
    private final StudyParticipantRepository studyParticipantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void approveParticipation(final Command command) {
        final Member applier = getApplier(command.studyId(), command.applierId());
        final Study study = queryStudyByIdService.findById(command.studyId());
        validateStudyInProgress(study);
        validateStudyCapacityIsAvailable(study);

        studyParticipantRepository.updateParticipantStatus(command.studyId(), command.applierId(), APPROVE);
        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(
                    new StudyApprovedEvent(
                            applier.getEmailValue(),
                            applier.getNicknameValue(),
                            study.getNameValue()
                    )
            );
        }
    }

    private Member getApplier(final Long studyId, final Long applierId) {
        return studyParticipantRepository.findApplier(studyId, applierId)
                .orElseThrow(() -> StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND));
    }

    private void validateStudyInProgress(final Study study) {
        if (!study.isStudyInProgress()) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_IS_FINISH);
        }
    }

    private void validateStudyCapacityIsAvailable(final Study study) {
        final int currentParticipants = studyParticipantRepository.getCurrentParticipantsCount(study.getId());

        if (study.isCapacityFull(currentParticipants)) {
            throw StudyWithMeException.type(StudyParticipantErrorCode.STUDY_CAPACITY_ALREADY_FULL);
        }
    }
}
