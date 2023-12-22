package com.kgu.studywithme.studyparticipant.domain.service;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyparticipant.domain.event.StudyApprovedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyGraduatedEvent;
import com.kgu.studywithme.studyparticipant.domain.event.StudyRejectedEvent;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.LEAVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.REJECT;

@Service
@RequiredArgsConstructor
public class ParticipationProcessor {
    private final StudyParticipantRepository studyParticipantRepository;
    private final ApplicationEventPublisher eventPublisher;

    @StudyWithMeWritableTransactional
    public void approveApplier(final Study study, final Member applier) {
        study.addParticipant();
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), APPROVE);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(new StudyApprovedEvent(
                    applier.getEmail().getValue(),
                    applier.getNickname().getValue(),
                    study.getName().getValue()
            ));
        }
    }

    @StudyWithMeWritableTransactional
    public void rejectApplier(final Study study, final Member applier, final String reason) {
        studyParticipantRepository.updateParticipantStatus(study.getId(), applier.getId(), REJECT);

        if (applier.isEmailOptIn()) {
            eventPublisher.publishEvent(new StudyRejectedEvent(
                    applier.getEmail().getValue(),
                    applier.getNickname().getValue(),
                    study.getName().getValue(),
                    reason
            ));
        }
    }

    @StudyWithMeWritableTransactional
    public void leaveStudy(final Study study, final Member participant) {
        study.removeParticipant();
        studyParticipantRepository.updateParticipantStatus(study.getId(), participant.getId(), LEAVE);
    }

    @StudyWithMeWritableTransactional
    public void graduateStudy(final Study study, final Member participant) {
        study.removeParticipant();
        studyParticipantRepository.updateParticipantStatus(study.getId(), participant.getId(), GRADUATED);

        if (participant.isEmailOptIn()) {
            eventPublisher.publishEvent(new StudyGraduatedEvent(
                    participant.getEmail().getValue(),
                    participant.getNickname().getValue(),
                    study.getName().getValue()
            ));
        }
    }
}
